import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.selects.*

private suspend fun myproducer(ch1: SendChannel<Int>,ch2: SendChannel<Int> )  {
    for (num in 1..10){// produce 10 numbers from 1 to 10
        delay(10)
        select<Unit>{
            ch1.onSend(num){}
            ch2.onSend(num){}
        }
    }
    ch2.close()
    ch1.close()

}
private suspend fun myposrednik(chin:ReceiveChannel<Int>,chout:SendChannel<Int>, nb:Int){
    chin.consumeEach{
        delay(10)
        println("posrednik $nb has $it")
        chout.send(it)
        if(it==10){
            chout.close()
        }
    }


}
private suspend fun konsument(chin:ReceiveChannel<Int>){
    chin.consumeEach{
        delay(10)
        println("Konsument has $it")
    }
}
fun main()=runBlocking<Unit>{
    val c1=Channel<Int>()
    val c2 = Channel<Int>()
    val cout=Channel<Int>()
    launch{
        myproducer(c1,c2)
    }
    launch{
        myposrednik(c1,cout,1)
    }
    launch{
        myposrednik(c2,cout,2)
    }
    launch{
        konsument(cout)
    }

}