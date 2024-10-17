package ch.heigvd.iict.daa.template

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ch.heigvd.iict.daa.labo3.Person
import ch.heigvd.iict.daa.template.R



class MainActivity : AppCompatActivity() {

    lateinit var client : Person
    var textInputs : List<TextView> = listOf()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val nameInput = findViewById<EditText>(R.id.nameInput)
        textInputs += nameInput

        val firstNameInput = findViewById<EditText>(R.id.firstNameInput)
        textInputs += firstNameInput


        nameInput.setHint(R.string.main_base_name_title)
        firstNameInput.setHint(R.string.main_base_firstname_title)

        //On click de l'occupation, assigner l'user a soit un employé ou bien un etudiant

        val occupationButton = findViewById<RadioButton>(R.id.occupationButton)
        occupationButton.setOnCheckedChangeListener{ _, checkedId ->
            when (checkedId){
                R.id.studenRadioButton -> {
                    //Show the student view and hide the employee view
                }
                R.id.employeeRadioButton -> {
                    //Show the employee view and hide the student view
                }
                else -> {
                    //Hide all the views
                }
            }
        }

        val emailInput = findViewById<EditText>(R.id.emailInput)
        textInputs += emailInput

        emailInput.setHint(R.string.additional_email_title)

        val commentInput = findViewById<EditText>(R.id.commentInput)


        val cancelButton = findViewById<Button>(R.id.cancelButton)
        cancelButton.setOnClickListener{
            for(TextView tv : textInputs){

        }
        }

    }



    // Fonction appelée lorsque l'activité est détruite, utilisée pour sauvegarder l'état lors d'une rotation par exemple
    override fun onDestroy(){
        super.onDestroy()

    }



}