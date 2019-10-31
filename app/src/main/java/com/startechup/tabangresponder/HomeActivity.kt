package com.startechup.tabangresponder

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.report_details.view.*


class HomeActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    private var location = mutableMapOf<String, Double>()

    private var isFirstLoaded = true
    private var acceptReport = true

    private var reportId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        db = FirebaseFirestore.getInstance()

        db.collection("responders").document("U85Obd8cEmvvBuoXruwa")
            .collection("reports")
            .addSnapshotListener(this) { test, test2 ->
                // We don't want to get data on first load
                // We'll just listen to the update
                if (!isFirstLoaded && acceptReport) {
                    test?.documentChanges?.first { it.type == DocumentChange.Type.ADDED }?.run {
                        acceptReport = false
                        val doc = this.document.get("reportReference", DocumentReference::class.java) as DocumentReference
                        doc.get().addOnSuccessListener(this@HomeActivity) { docData ->
                            location["long"] = docData.getDouble("lng") ?: 0.0
                            location["lat"] = docData.getDouble("lat") ?: 0.0
                            reportId = docData.id
                            showDialog(docData.getString("desc") ?: "unknown message")
                        }
                    }
                } else {
                    isFirstLoaded = false
                }
            }

        reportView.button_arrived.setOnClickListener {
            reportView.button_arrived.visibility = View.GONE
            reportView.button_new_reports.visibility = View.VISIBLE

            db
                .collection("hospitals")
                .document("Ffv7QjIh6VbiLiYTl8An")
                .collection("reports")
                .document(reportId)
                .collection("actions")
                .add(mapOf(
                    "desc" to "I arrived at the location",
                    "responderName" to "Rhusfer John Cuezon",
                    "time" to Timestamp.now()
                ))
        }

        reportView.fab_direction.setOnClickListener {
            showDirection(location["long"] ?: 0.0, location["lat"] ?: 0.0)
        }

        reportView.button_new_reports.setOnClickListener {
            reportView.button_arrived.visibility = View.VISIBLE
            reportView.button_new_reports.visibility = View.GONE
            reportView.visibility = View.GONE

            acceptReport = true
        }
    }

    private fun showDirection(long: Double, lat: Double) {
        // Create a Uri from an intent string. Use the result to create an Intent.
        val gmmIntentUri = Uri.parse("google.navigation:q=$lat,$long")

        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        // Make the Intent explicit by setting the Google Maps package
        mapIntent.setPackage("com.google.android.apps.maps")

        // Attempt to start an activity that can handle the Intent
        startActivity(mapIntent)
    }

    private fun showDialog(message: String) {
        val dialog = MyAlertDialogFragment.instance(message)
        dialog.setAcceptListener {
            reportView.visibility = View.VISIBLE

            reportView.textView_description.text = message

            db
                .collection("hospitals")
                .document("Ffv7QjIh6VbiLiYTl8An")
                .collection("reports")
                .document(reportId)
                .collection("actions")
                .add(mapOf(
                    "desc" to "We'll be there in 5 mins",
                    "responderName" to "Rhusfer John Cuezon",
                    "time" to Timestamp.now()
                ))
        }
        dialog.showDialog(supportFragmentManager)
    }
}
