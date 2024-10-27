package no.uio.ifi.titanic.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header

class Client {
    //client for communication with MET APIs through IFI's proxy server
    val proxyClient: HttpClient = HttpClient {
        defaultRequest {
            url("https://gw-uio.intark.uh-it.no/in2000/")
            //nøkkel til api
            header("X-Gravitee-API-Key", "727ca5f7-7022-49be-8806-6d28cafa0f5c")
        }
    }

    //client for communication with the 'Se havniva' API
    val havnivaaClient: HttpClient = HttpClient{
        defaultRequest {
            url("https://api.sehavniva.no/")
        }
    }
}