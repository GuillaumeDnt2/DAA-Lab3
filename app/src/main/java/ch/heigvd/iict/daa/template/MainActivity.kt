package ch.heigvd.iict.daa.template

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.Group
import ch.heigvd.iict.daa.labo3.Person
import ch.heigvd.iict.daa.labo3.Student
import ch.heigvd.iict.daa.labo3.Worker
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Calendar
import java.util.Date

/**
 * Activité principale de l'application.
 * Permet de saisir des informations sur une personne.
 * La personne peut être un étudiant ou un employé.
 *
 * Actuellement, le résultat s'affiche uniquement dans la console de l'appareil
 *
 @author Junod Arthur
 @author Dunant Guillaume
 @author Häffner Edwin
 */
class MainActivity : AppCompatActivity() {

    lateinit var client : Person
    var textInputs : List<TextView> = listOf()
    var todayMillis = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli()
    var selectedNationality : String = ""
    var selectedSector : String = ""


    val calendarConstraint = CalendarConstraints.Builder()
        .setEnd(todayMillis)
        .setStart(LocalDateTime.now().minusYears(110).toInstant(ZoneOffset.UTC).toEpochMilli())
        .build()

    val datePicker = MaterialDatePicker.Builder
        .datePicker()
        .setCalendarConstraints(calendarConstraint)
        .build()

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

        val commentInput = findViewById<EditText>(R.id.additional_comment_edit)
        textInputs += commentInput

        //Set des hints des inputs
        nameInput.setHint(R.string.main_base_name_title)
        firstNameInput.setHint(R.string.main_base_firstname_title)

        //Input de la date de naissance
        val birthdayInput = findViewById<EditText>(R.id.base_birthday_edit)
        textInputs += birthdayInput

        //Ajout du bouton de birthday
        val birthdayButton = findViewById<ImageButton>(R.id.base_birthday_button)
        birthdayButton.setOnClickListener {
            datePicker.show(supportFragmentManager, "birthday_picker")
        }

        //Créer un calendrier pour la date de naissance
        val birthdayCalendar = Calendar.getInstance()

        //Listener du datePicker
        datePicker.addOnPositiveButtonClickListener {
            val selectedDateInMillis = datePicker.selection

            // Convertir en Date pour l'afficher
            if (selectedDateInMillis != null) {
                if (selectedDateInMillis > todayMillis) {
                    Toast.makeText(birthdayInput.context, "Choisissez une date de naissance valide !", Toast.LENGTH_SHORT).show()
                }
                else {
                    val date = Date(selectedDateInMillis)
                    val formattedDate = formatDateForDisplay(birthdayInput.context, date)
                    birthdayInput.setText(formattedDate)

                    // Mettre à jour le calendrier avec la date sélectionnée
                    birthdayCalendar.timeInMillis = selectedDateInMillis
                }
            }
        }

        //Spinner de la nationalité
        val spinnerNationalities = findViewById<Spinner>(R.id.base_nationality_spinner)
        val nationalities = resources.getStringArray(R.array.nationalities)
        ArrayAdapter.createFromResource(
            this,
            R.array.nationalities,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerNationalities.adapter = adapter
        }

        spinnerNationalities.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent : AdapterView<*>?, view: View?, position : Int, id : Long) {
                if (position == 0) {
                    selectedNationality = ""
                }else{
                    selectedNationality = nationalities[position]
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedNationality = ""
            }
        }

        //Spinner du secteur
        val spinnerSectors = findViewById<Spinner>(R.id.worker_sector_spinner)
        val sectors = resources.getStringArray(R.array.sectors)
        ArrayAdapter.createFromResource(
            this,
            R.array.sectors,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerSectors.adapter = adapter
        }

        spinnerSectors.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent : AdapterView<*>?, view: View?, position : Int, id : Long) {
                if (position == 0) {
                    selectedSector = ""
                }else{
                    selectedSector = sectors[position]
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedSector = ""
            }
        }

        val occupationRadioGroup = findViewById<RadioGroup>(R.id.base_occupation_radio_group)
        val constraintLayout = findViewById<ConstraintLayout>(R.id.main)
        val companyNameTextView = findViewById<TextView>(R.id.additional_title)
        val studentGroup = findViewById<Group>(R.id.student_group)
        val workerGroup = findViewById<Group>(R.id.worker_group)

        occupationRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.base_occupation_radio_button_student -> {

                    // Rendre le groupe d'étudiant visible, masquer le groupe d'employé
                    studentGroup.visibility = View.VISIBLE
                    workerGroup.visibility = View.GONE

                    // Ajustement du bloc des données complémentaires
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

                    // Rendre le groupe d'employé visible, masquer le groupe d'étudiant
                    studentGroup.visibility = View.GONE
                    workerGroup.visibility = View.VISIBLE

                    // Ajustement du bloc des données complémentaires
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
                    //Do nothing
                }
            }
        }

        val emailInput = findViewById<EditText>(R.id.emailInput)
        textInputs += emailInput

        emailInput.setHint(R.string.additional_email_title)


        val cancelButton = findViewById<Button>(R.id.additional_cancel_button)
        cancelButton.setOnClickListener {

            //Reset des inputs de text
            for (textView in textInputs) {
                textView.text = ""
            }


            findViewById<RadioGroup>(R.id.base_occupation_radio_group).clearCheck()
            studentGroup.visibility = View.GONE
            workerGroup.visibility = View.GONE

            //Reset les valeurs des spinners
            spinnerNationalities.setSelection(0)
            spinnerSectors.setSelection(0)
        }

        //Listener du bouton de validation
        val okButton = findViewById<Button>(R.id.additional_ok_button)
        okButton.setOnClickListener {
            if (occupationRadioGroup.checkedRadioButtonId == R.id.base_occupation_radio_button_student) {

                if (nameInput != null && firstNameInput != null && schoolInput != null && graduationYearInput != null && emailInput != null && commentInput != null) {

                    client = Student(
                        nameInput.text.toString(),
                        firstNameInput.text.toString(),
                        birthdayCalendar,
                        selectedNationality,
                        schoolInput.text.toString(),
                        graduationYearInput.text.toString().toInt(),
                        emailInput.text.toString(),
                        commentInput.text.toString()
                    )
                }

            } else if (occupationRadioGroup.checkedRadioButtonId == R.id.base_occupation_radio_button_worker) {
                if (nameInput != null && firstNameInput != null && companyInput != null && experienceInput != null && emailInput != null && commentInput != null) {
                    client = Worker(
                        nameInput.text.toString(),
                        firstNameInput.text.toString(),
                        birthdayCalendar,
                        selectedNationality,
                        companyInput.text.toString(),
                        selectedSector,
                        experienceInput.text.toString().toInt(),
                        emailInput.text.toString(),
                        commentInput.text.toString()
                    )
                }
            }

            if (::client.isInitialized) {
                println(client.toString())
            } else {
                println("Client not initialized !")
            }

        }
    }

    /**
     * Formatte une date pour l'affichage selon le système de date du téléphone.
     */
    fun formatDateForDisplay(context: Context, date: Date): String {
        val locale = context.resources.configuration.locales[0]
        val dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)

        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(dateFormat)
    }

}


