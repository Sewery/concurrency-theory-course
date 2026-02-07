package com.seweryn

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

private suspend fun myproducer(ch1:SendChannel<Int>){
    for(num in 1..10){
        delay(10)
        println("producent produkuje $num")
        ch1.send(num)
    }
    ch1.close()

}
private suspend fun przetwarzacz(chin:ReceiveChannel<Int>,chout:SendChannel<Int>, nb:Int){
    chin.consumeEach{
        delay(10)
        println("przetwarzacz $nb przetwarza $it")
        chout.send(it)
        if(it==10){
            chout.close()
        }
    }
}
private suspend fun konsument(chin:ReceiveChannel<Int>){
    chin.consumeEach{
        delay(10)
        println("Konsument odbiera $it")
    }
}
fun main()=runBlocking<Unit>{
    val N = 8;
    val tab_channel = mutableListOf<Channel<Int>>()
    for(num in 1..N){
        tab_channel.add(Channel<Int>())
    }

    launch{
        myproducer(tab_channel[0])
    }
    launch{
        przetwarzacz(tab_channel[0],tab_channel[1],1)
    }
    for(num in 1..N-2){
        launch{
            przetwarzacz(tab_channel[num],tab_channel[num+1],num)
        }
    }
    launch{
        konsument(tab_channel[N-1])
    }

}