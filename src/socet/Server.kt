package com.martynov.socet

import com.google.gson.Gson
import com.martynov.FILE_SOCKET
import com.martynov.FILE_USER
import java.io.File
import java.io.IOException
import java.net.ServerSocket

class Server {
    companion object{
        var serverList = ArrayList<ServerSomthing>()
    }

    val PORT: Int = 8080


    init {
        val server = ServerSocket(PORT)
        println("Server Started");
        try {
            while (true) {
                val socket = server.accept()
                try {
                    serverList.add(ServerSomthing(socket))
                    File(FILE_SOCKET).writeText(Gson().toJson(serverList.toString()))
                }catch (e: IOException){
                    socket.close()
                }
            }

        }finally {
            server.close()
        }
    }
}