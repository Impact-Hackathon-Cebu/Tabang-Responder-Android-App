package com.startechup.tabangresponder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.report_details.view.*
import java.lang.ref.Reference
import android.content.Intent
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.Uri


class HomeActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var responderData: DocumentSnapshot

    private var location = mutableMapOf<String, Double>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        db = FirebaseFirestore.getInstance()

        db.collection("responders").document("U85Obd8cEmvvBuoXruwa")
            .collection("reports")
            .addSnapshotListener(this) { test, test2 ->
                test?.documentChanges?.forEach { document ->
                    if (document.type == DocumentChange.Type.ADDED) {
                        val doc = document.document.get("test", DocumentReference::class.java) as DocumentReference
                        doc.get().addOnSuccessListener(this) {
                            location["long"] = it.getDouble("lng") ?: 0.0
                            location["lat"] = it.getDouble("lat") ?: 0.0
                            showDialog(it.getString("desc") ?: "unknown message")
                        }
                    }
                }
            }

        reportView.button_arrived.setOnClickListener {
            reportView.button_arrived.visibility = View.GONE
            reportView.button_new_reports.visibility = View.VISIBLE
        }

        reportView.fab_direction.setOnClickListener {
            showDirection(location["long"] ?: 0.0, location["lat"] ?: 0.0)
        }

        reportView.button_new_reports.setOnClickListener {
            reportView.button_arrived.visibility = View.VISIBLE
            reportView.button_new_reports.visibility = View.GONE
            reportView.visibility = View.GONE
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
        }
        dialog.showDialog(supportFragmentManager)
    }
}
