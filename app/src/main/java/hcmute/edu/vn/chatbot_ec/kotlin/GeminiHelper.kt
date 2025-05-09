package hcmute.edu.vn.chatbot_ec.helper

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object GeminiHelper {
    @JvmStatic
    fun generateResponse(prompt: String, apiKey: String, modelName: String, callback: (String?, Exception?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val model = GenerativeModel(modelName = modelName, apiKey = apiKey)
                val response = model.generateContent(prompt)
                val text = response.text
                callback(text, null)
            } catch (e: Exception) {
                Log.e("GeminiHelper", "Error: ${e.message}")
                callback(null, e)
            }
        }
    }
}
