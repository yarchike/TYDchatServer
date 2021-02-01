package com.martynov.socet

import java.io.*
import java.net.Socket

class ServerSomthing(socket: Socket): Thread() {

    var reader: BufferedReader? = null
    var writer: BufferedWriter? = null

    init {
        reader = BufferedReader(InputStreamReader(socket.getInputStream()))
        writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
        start()
    }

    override fun run() {
        var word: String? = null
        try{
            while (true){
                word = reader?.readLine()
                println(word)
                if(word.equals("stop")){
                    break
                }
                for(vr: ServerSomthing in Server.serverList){
                    vr.send(word.toString())
                }
            }
        }catch (e: IOException){

        }
    }
    private fun send(msg: String){
        try{
            writer?.write("$msg + \n")
            writer?.flush()
        }catch (e: IOException){

        }
    }
}