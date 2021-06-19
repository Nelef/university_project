package kr.ac.kumoh.ce.university_project_note_ver1.ui.timeline


import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(s: String) {
        super.onNewToken(s)
        println("NEW_TOKEN :::::::::::::::::::::::::: $s")
    }
}


