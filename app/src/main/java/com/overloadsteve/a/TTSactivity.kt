package com.overloadsteve.a

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale
import java.util.concurrent.LinkedBlockingQueue

class TTSactivity(context: Context) : TextToSpeech.OnInitListener{

    private var tts: TextToSpeech = TextToSpeech(context, this)
    private var isTtsInitialized: Boolean = false
    private var queue = LinkedBlockingQueue<String>()
    private var isSpeaking = false

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.US)
            isTtsInitialized = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
            if (!isTtsInitialized) {
                Log.e("TTS", "Language not supported")
            }
        } else {
            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    TODO("Not yet implemented")
                }

                override fun onDone(utteranceId: String?) {
                    queue.poll()
                    isSpeaking = false
                    speakNext()
                }

                override fun onError(utteranceId: String?) {
                    Log.e(TAG, "Error in speech")
                }
            })
            Log.e(TAG, "Initialization Failed")
        }
    }
    fun speak(text: String) {
        if (isTtsInitialized) {
            queue.offer(text)
            if(!isSpeaking){
                speakNext()
            }
        }
    }

    private fun speakNext() {
        if(queue.isEmpty()) {
            val text = queue.peek()
            tts.speak(text, TextToSpeech.QUEUE_FLUSH,null,"tts1")
            isSpeaking = true
        }
    }

    fun shutdown() {
        tts.shutdown()
    }

    companion object{
        private const val TAG = "TTS"
    }
}