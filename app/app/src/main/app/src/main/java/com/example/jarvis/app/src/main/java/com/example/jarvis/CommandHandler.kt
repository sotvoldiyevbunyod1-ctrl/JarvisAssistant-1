package com.example.jarvis

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class CommandHandler(
    private val context: Context,
    private val tts: TextToSpeech
) {
    private val appLauncher = AppLauncher(context)

    fun handle(rawText: String) {
        val text = rawText.trim().lowercase(Locale("ru"))

        when {
            text.startsWith("открой ") || text.startsWith("запусти ") -> {
                val appName = text.removePrefix("открой ").removePrefix("запусти ").trim()
                val opened = appLauncher.openAppByName(appName)
                speak(if (opened) "Открываю $appName" else "Не нашёл приложение $appName")
            }

            text.startsWith("позвони ") || text.startsWith("набери ") -> {
                val target = text.removePrefix("позвони ").removePrefix("набери ").trim()
                appLauncher.dial(target)
                speak("Набираю $target")
            }

            text.startsWith("найди в интернете ") || text.startsWith("погугли ") -> {
                val query = text.removePrefix("найди в интернете ").removePrefix("погугли ").trim()
                appLauncher.webSearch(query)
                speak("Ищу $query")
            }

            text.startsWith("открой сайт ") -> {
                val site = text.removePrefix("открой сайт ").trim()
                val url = if (site.startsWith("http")) site else "https://$site"
                appLauncher.openUrl(url)
                speak("Открываю $site")
            }

            text.contains("открой настройки") -> {
                appLauncher.openSettings()
                speak("Открываю настройки")
            }

            text.contains("открой камеру") -> {
                appLauncher.openCamera()
                speak("Открываю камеру")
            }

            text.contains("привет") -> {
                speak("Привет! Я слушаю.")
            }

            else -> {
                speak("Я не понял команду: $text")
            }
        }
    }

    private fun speak(message: String) {
        tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, null)
    }
}
