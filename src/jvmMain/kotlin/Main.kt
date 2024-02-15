// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.amazon.paapi5.v1.*
import com.amazon.paapi5.v1.api.DefaultApi


fun callAmazon(asin: String) : String
{
    // see https://webservices.amazon.com/paapi5/documentation/with-sdk.html

    var txt = ""
    val client = ApiClient()
    client.accessKey = System.getenv("PA_ACCESS_KEY")
    client.secretKey = System.getenv("PA_SECRET_KEY")

    val partnerTag = System.getenv("PA_ASSOCIATE_TAG")

    client.host = "webservices.amazon.co.jp"
    client.region = "us-west-2"

    val api = DefaultApi(client)

    val searchItemsResources: MutableList<SearchItemsResource> = ArrayList()
    searchItemsResources.add(SearchItemsResource.IMAGES_PRIMARY_SMALL)
    searchItemsResources.add(SearchItemsResource.ITEMINFO_TITLE)
    val searchIndex = "All"

    // Specify keywords
    val keywords = asin // "B018WNIBJS"
    val searchItemsRequest = SearchItemsRequest().partnerTag(partnerTag).keywords(keywords)
        .searchIndex(searchIndex).resources(searchItemsResources).partnerType(PartnerType.ASSOCIATES)

    try {
        // Forming the request
        val response = api.searchItems(searchItemsRequest)
        // txt +="Complete response: $response\n" // for debug

        // Parsing the request
        if (response.searchResult != null)
        {
            val item: Item? = response.searchResult.items[0]
            if (item != null)
            {
                if (item.detailPageURL != null) {
                    txt += "<a href=\"" + item.detailPageURL + "\">\n"
                }
                if (item.images != null)
                {
                    val imagesURL = item.images.primary.small.url
                    txt += "<img src=\"$imagesURL\">\n"
                }
                if (item.itemInfo != null)
                {
                    txt += item.itemInfo.title.displayValue + "\n"
                }
                txt += "</a>\n"
            }
        }

        if (response.errors != null) {
            txt += "Printing errors:\nPrinting Errors from list of Errors\n"
            for (error in response.errors) {
                txt += "Error code: " + error.code + "\n"
                txt += "Error message: " + error.message + "\n"
            }
        }
    } catch (exception: ApiException) {
        // Exception handling
        txt += "Error calling PA-API 5.0!" + "\n"
        txt += "Status code: " + exception.code + "\n"
        txt += "Errors: " + exception.responseBody + "\n"
        txt += "Message: " + exception.message + "\n"
        if (exception.responseHeaders != null) {
            // Printing request reference
            txt += "Request ID: " + exception.responseHeaders["x-amzn-RequestId"] + "\n"
        }
        // exception.printStackTrace();
    } catch (exception: Exception) {
        txt += "Exception message: " + exception.message  + "\n"
        // exception.printStackTrace();
    }
    return txt
}

@Composable
@Preview
fun App() {

    MaterialTheme {
        var asin by remember { mutableStateOf("") }
        var text by remember { mutableStateOf("") }
        Column {
            Row {
                Text("ASIN: ",
                    modifier = Modifier.width(70.dp).align(alignment = Alignment.CenterVertically)
                        .padding(all = 4.dp),
                    fontSize = 16.sp
                )
                TextField(
                    value = asin,
                    onValueChange = { asin = it },
                    modifier = Modifier.padding(all = 4.dp)
                        .width(200.dp),
                        maxLines = 1,
                        singleLine = true,
                    )
                Button(onClick = {
                    text = callAmazon(asin)
                }) {
                    Text(
                        "Get",
                        fontSize = 16.sp,
                    )
                }
            }
            TextField(
                value = text,
                onValueChange = { text = it }, modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(10.dp)
                    .border(width = 1.dp, color = Color.Black)
            )
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
