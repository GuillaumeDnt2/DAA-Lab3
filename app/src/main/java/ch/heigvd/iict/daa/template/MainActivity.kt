package ch.heigvd.iict.daa.template

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.Group
import ch.heigvd.iict.daa.labo3.Person
import ch.heigvd.iict.daa.template.R
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.Locale
import kotlin.text.format


class MainActivity : AppCompatActivity() {

    lateinit var client : Person
    var textInputs : List<TextView> = listOf()
    val datePicker = MaterialDatePicker.Builder.datePicker().build()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val nameInput = findViewById<EditText>(R.id.base_name_edit)
        textInputs += nameInput

        val firstNameInput = findViewById<EditText>(R.id.base_firstname_edit)
        textInputs += firstNameInput

        val schoolInput = findViewById<EditText>(R.id.student_school_edit)
        textInputs += schoolInput

        val graduationYearInput = findViewById<EditText>(R.id.student_graduationyear_edit)
        textInputs += graduationYearInput

        val companyInput = findViewById<EditText>(R.id.worker_company_edit)
        textInputs += companyInput

        val experienceInput = findViewById<EditText>(R.id.worker_experience_edit)
        textInputs += experienceInput

        val commentInpt = findViewById<EditText>(R.id.additional_comment_edit)
        textInputs += commentInpt


        nameInput.setHint(R.string.main_base_name_title)
        firstNameInput.setHint(R.string.main_base_firstname_title)

        //Input de la date de naissance
        val birthdayInput = findViewById<EditText>(R.id.base_birthday_edit)
        textInputs += birthdayInput

        //Ajout du bouton de birthday
        val birthdayButton = findViewById<ImageButton>(R.id.base_birthday_button)
        birthdayButton.setOnClickListener{
            datePicker.show(supportFragmentManager, "birthday_picker")
        }

        datePicker.addOnPositiveButtonClickListener {
            val selectedDateInMillis = datePicker.selection
            if (selectedDateInMillis != null) {
                val formatter =  SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                birthdayInput.setText(formatter.format(selectedDateInMillis))
            }
        }

        val spinnerNationalities = findViewById<Spinner>(R.id.base_nationality_spinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.nationalities,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerNationalities.adapter = adapter
        }

        val spinnerSectors = findViewById<Spinner>(R.id.worker_sector_spinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.sectors,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerSectors.adapter = adapter
        }

        //On click de l'occupation, assigner l'user a soit un employé ou bien un etudiant

        val occupationRadioGroup = findViewById<RadioGroup>(R.id.base_occupation_radio_group)
        val constraintLayout = findViewById<ConstraintLayout>(R.id.main)
        val companyNameTextView = findViewById<TextView>(R.id.additional_title)
        val studentGroup = findViewById<Group>(R.id.student_group)
        val workerGroup = findViewById<Group>(R.id.worker_group)

        occupationRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.base_occupation_radio_button_student -> {
                    // Show student group, hide worker group
                    studentGroup.visibility = View.VISIBLE
                    workerGroup.visibility = View.GONE

                    // Adjust constraints programmatically
                    val constraintSet = ConstraintSet()
                    constraintSet.clone(constraintLayout)
                    constraintSet.connect(
                        companyNameTextView.id,
                        ConstraintSet.TOP,
                        R.id.student_graduationyear_edit,
                        ConstraintSet.BOTTOM,

                    )
                    constraintSet.applyTo(constraintLayout)
                }
                R.id.base_occupation_radio_button_worker -> {
                    // Show worker group, hide student group
                    studentGroup.visibility = View.GONE
                    workerGroup.visibility = View.VISIBLE

                    // Adjust constraints programmatically
                    val constraintSet = ConstraintSet()
                    constraintSet.clone(constraintLayout)
                    constraintSet.connect(
                        companyNameTextView.id,
                        ConstraintSet.TOP,
                        R.id.worker_experience_edit,
                        ConstraintSet.BOTTOM,
                    )
                    constraintSet.applyTo(constraintLayout)
                }
                else -> {
                    //Hide all the views

                }
            }
        }

        val emailInput = findViewById<EditText>(R.id.emailInput)
        textInputs += emailInput

        emailInput.setHint(R.string.additional_email_title)

        val commentInput = findViewById<EditText>(R.id.additional_comment_edit)


        val cancelButton = findViewById<Button>(R.id.additional_cancel_button)
        cancelButton.setOnClickListener{
            for(textView in textInputs){
                textView.text = ""
            }

            findViewById<RadioGroup>(R.id.base_occupation_radio_group).clearCheck()
            studentGroup.visibility = View.GONE
            workerGroup.visibility = View.GONE
        }

    }



    // Fonction appelée lorsque l'activité est détruite, utilisée pour sauvegarder l'état lors d'une rotation par exemple
    override fun onDestroy(){
        super.onDestroy()

    }



}