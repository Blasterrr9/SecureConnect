package com.example.ciscog

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.example.ciscog.adapters.MessageAdapter
import com.example.ciscog.models.Message

import okhttp3.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class ChatActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var returnButton: ImageButton
    private lateinit var messageList: MutableList<Message>
    private lateinit var messageAdapter: MessageAdapter

    companion object {
        val JSON = "application/json; charset=utf-8".toMediaType()
    }

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        messageList = mutableListOf()

        recyclerView = findViewById(R.id.recycler_view)
        messageEditText = findViewById(R.id.message_edit_text)
        sendButton = findViewById(R.id.send_btn)

        // setup recycler view
        messageAdapter = MessageAdapter(messageList)
        recyclerView.adapter = messageAdapter
        val llm = LinearLayoutManager(this)
        llm.stackFromEnd = true
        recyclerView.layoutManager = llm

        returnButton = findViewById(R.id.return_btn)
        returnButton.setOnClickListener {
            finish()
        }

        // Obtener el intent y agregar el primer mensaje
        val config_info = intent.getStringExtra("config_info")
        if (!config_info.isNullOrEmpty()) {
            addToChat(config_info, Message.SENT_BY_ME)
            callAPI(config_info)
        }

        sendButton.setOnClickListener {
            val question = messageEditText.text.toString().trim()

            if (question.isNotEmpty()) {
                addToChat(question, Message.SENT_BY_ME)
                messageEditText.setText("")
                callAPI(question)
            } else {
                addToChat("Por favor, escribe una pregunta", Message.SENT_BY_BOT)
            }


        }
    }

    private fun addToChat(message: String, sentBy: String) {
        runOnUiThread {
            messageList.add(Message(message, sentBy))
            messageAdapter.notifyDataSetChanged()
            recyclerView.smoothScrollToPosition(messageAdapter.itemCount)
        }
    }

    private fun addResponse(response: String) {
        messageList.removeAt(messageList.size - 1)
        addToChat(response, Message.SENT_BY_BOT)
    }

    private fun callAPI(question: String) {
        // okhttp
        messageList.add(Message("Typing... ", Message.SENT_BY_BOT))

        val jsonBody = JSONObject()
        try {
            jsonBody.put("model", "babbage-002")
            jsonBody.put("prompt", question)
            jsonBody.put("max_tokens", 4000)
            jsonBody.put("temperature", 0)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val body = RequestBody.create(JSON, jsonBody.toString())
        val request = Request.Builder()
            .url("https://api.openai.com/v1/completions")
            .header("Authorization", "Bearer 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")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                addResponse("Failed to load response due to ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    try {
                        val jsonObject = JSONObject(response.body?.string() ?: "")
                        val jsonArray: JSONArray = jsonObject.getJSONArray("choices")
                        val result = jsonArray.getJSONObject(0).getString("text")
                        addResponse(result.trim())
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    addResponse("Failed to load response due to ${response.body?.string()}")
                }
            }
        })
    }
}
