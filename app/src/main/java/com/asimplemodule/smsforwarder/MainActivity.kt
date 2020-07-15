package com.asimplemodule.smsforwarder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private val mViewModel: WorkerAwareViewModel by viewModels()
    val REQUIRED_SDK_PERMISSIONS = arrayOf(
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.READ_SMS,
        Manifest.permission.RECEIVE_SMS
    )
    val REQUEST_CODE_ASK_PERMISSIONS = 321;
    val MAX_LIMIT = 10


    fun updateList(repo: IncommingMessagesRepo) {
        val processedMessages: List<Map<String, String>> = repo.getLast(MAX_LIMIT)
        val listView = findViewById<ListView>(R.id.listData)
        val messageRowStringList: List<String> = processedMessages.map {
            IncommingMessagesRepoContants.COLS.fold(
                "",
                { acc, s -> "${acc}${it.get(s)} \n" })
        }
        val adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            messageRowStringList
        )
        listView.adapter = adapter
    }

    fun updateProcessLabel(repo: IncommingMessagesRepo){
        val procesados = findViewById<TextView>(R.id.procesados)
        val count = repo.countMessages()
        procesados.text = "Procesados ${count}"
    }

    fun updateUi(repo: IncommingMessagesRepo){
        updateList(repo)
        updateProcessLabel(repo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermissions()
        val repo = IncommingMessagesRepo(applicationContext)
        updateUi(repo)


        mViewModel.workStatus.observe(this, Observer { infoList ->
            if (infoList == null || infoList.isEmpty()) {
                return@Observer
            }
            val info = infoList.first()
            // val finished = info.state.isFinished
            updateUi(repo)
        })
    }

    fun checkPermissions() {
        val missingPermissions = ArrayList<String>()
        // check all required dynamic permissions
        for (permission in REQUIRED_SDK_PERMISSIONS) {
            val result = checkSelfPermission(permission)
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission)
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            val permissions = missingPermissions
                .toTypedArray()
            requestPermissions(permissions, REQUEST_CODE_ASK_PERMISSIONS)
        } else {
            val grantResults = IntArray(REQUIRED_SDK_PERMISSIONS.size)
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED)
            onRequestPermissionsResult(
                REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                grantResults
            )
        }
    }

}
