# DAA - Laboratoire n°3

Auteurs : Junod Arthur, Dunant Guillaume, Häffner Edwin

Date : 1 Novembre 2024

Classe : A

Groupe : A2

---

# Questions

## Question 1

> Pour le champ remark, destiné à accueillir un texte pouvant être plus long qu’une seule ligne, quelle configuration particulière faut-il faire dans le fichier XML pour que son comportement soit correct ? Nous pensons notamment à la possibilité de faire des retours à la ligne, d’activer le correcteur orthographique et de permettre au champ de prendre la taille nécessaire.

Pour permettre à l'utilisateur d'entrer plusieurs lignes, d'activer la correction orthographique, ... il faut le définir dans le paramètre `inputType` et séparer les différents flags avec le caractère `|`. Par exemple :
```xml
<EditText
    ...
    android:inputType="textMultiLine|textAutoCorrect"
    ...
    />
```

## Question 2

> Pour afficher la date sélectionnée via le DatePicker, nous pouvons utiliser un DateFormat permettant par exemple d’afficher 12 juin 1996 à partir d’une instance de Date. Le formatage des dates peut être relativement différent en fonction des langues, la traduction des mois par exemple, mais également des habitudes régionales différentes : la même date en anglais britannique serait 12th June 1996 et en anglais américain June 12, 1996. Comment peut-on gérer cela au mieux ?

Pour gérer les différentes manières de formater les dates selon la région dans laquelle l'utilisateur se trouve, on peut récupérer le `Locale` de l'utilisateur et l'utiliser pour formater la date. Par exemple dans notre code nous avons utilisé cette fonction :
```kotlin
fun formatDateForDisplay(context: Context, date: Date): String {
        val locale = context.resources.configuration.locales[0]
        val dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)

        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(dateFormat)
    }
```

Le contexte est utilisé pour récupérer la locale de l'utilisateur, puis on formate la date en utilisant cette locale. On peut modifier le display de la date en changeant le `FormatStyle` avec les valeurs `SHORT`, `MEDIUM`, `LONG` ou `FULL`.

Ici `MEDIUM` donne ce qui était demandé dans l'énoncé du laboratoire.


## Question 3

> Si vous avez utilisé le MaterialDatePicker2 de la librairie Material. Est-il possible de limiter les dates sélectionnables dans le dialogue, en particulier pour une date de naissance il est peu probable d’avoir une personne née il y a plus de 110 ans ou à une date dans le futur. Comment pouvons-nous mettre cela en place ?

Oui, pour limiter les dates sélectionnables, il faut ajouter un `CalendarConstraints`. Pour cela, il faut d'abord le créer en lui passant la date de début et de fin souhaités puis l'ajouter à la construction du calendrier.
```kotlin
var todayMillis = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli()

val calendarConstraint = CalendarConstraints.Builder()
    .setEnd(todayMillis)
    .setStart(LocalDateTime.now().minusYears(110).toInstant(ZoneOffset.UTC).toEpochMilli())
    .build()
```

```kotlin
val datePicker = MaterialDatePicker.Builder
    .datePicker()
    .setCalendarConstraints(calendarConstraint)
    .build()
```

Mais après cette implémentation, nous pouvions toujours sélectionner des jours dans le mois actuel qui se trouvaient dans le futur, il a fallut donc rajouter une nouvelle vérification pour les jours sous cette forme:

```kotlin
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
```

## Question 4

> Lors du remplissage des champs textuels, vous pouvez constater que le bouton « suivant » présent sur le clavier virtuel permet de sauter automatiquement au prochain champ à saisir, cf. Fig. 2. Est-ce possible de spécifier son propre ordre de remplissage du questionnaire ? Arrivé sur le dernier champ, est-il possible de faire en sorte que ce bouton soit lié au bouton de validation du questionnaire ?
>
>Hint : Le champ remark, multilignes, peut provoquer des effets de bords en fonction du clavier virtuel utilisé sur votre smartphone. Vous pouvez l’échanger avec le champ e-mail pour faciliter vos recherches concernant la réponse à cette question.

Pour spécifier l'ordre de remplissage des champs, on peut utiliser l'attribut `android:nextFocusDown` pour spécifier le champ suivant à sélectionner. Par exemple dans notre `activity_main.xml` nous avons ceci pour passer au bouton `OK` au lieu du bouton `CANCEL`:

```xml
android:nextFocusDown="@+id/additional_ok_button"
```

## Question 5

>  Pour les deux Spinners (nationalité et secteur d’activité), comment peut-on faire en sorte que le premier choix corresponde au choix null, affichant par exemple le label « Sélectionner » ? Comment peut-on gérer cette valeur pour ne pas qu’elle soit confondue avec une réponse ?

On rajoute la possibilité de choisir "Sélectionner" dans le `Spinner`, puis, grâce à un nouvel object qui implémente l'interface `AdapterView.OnItemSelectedListener` qui va remplacer celle du spinner que l'on veut modifier, on pourra surcharger les fonctions qui seront appelées quand nous sélectionnons une réponse par exemple. C'est dans ces fonctions que l'on pourra faire une vérification de si nous nous trouvons à la position de la réponse "Sélectionner" (souvent en première ou dernière position) et gérer spécifiquement son cas comme mettre la nationalité/le secteur d'activité à `null`.

````kotlin
val nationalities = resources.getStringArray(R.array.nationalities)

spinnerNationalities.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent : AdapterView<*>?, view: View?, position : Int, id : Long) {
                if (position == 0) {
                    // Gérer la sélection de "Sélectionner"
                }else{
                    val selectedNationality = nationalities[position]
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Gérer si on ne sélectionne rien
            }
        }
````