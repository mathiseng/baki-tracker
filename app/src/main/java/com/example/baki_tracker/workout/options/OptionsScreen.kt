package com.example.baki_tracker.workout.options

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.example.baki_tracker.R
import com.example.baki_tracker.model.workout.WorkoutType

@Composable
fun OptionsScreen(
    elementName: String,
    name: String,
    exerciseNumber: Int,
    workoutType: WorkoutType? = null,
    date: String? = null,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Column {
        //Header

        OptionsHeader(name, exerciseNumber, workoutType, date)
        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
        //Body
        Column(Modifier.padding(horizontal = 24.dp)) {
            OptionsItem(
                Icons.Outlined.Edit, "${stringResource(R.string.edit)} $elementName", onEditClick
            )
            OptionsItem(Icons.Outlined.Delete,
                "${stringResource(R.string.delete)} $elementName",
                { onDeleteClick() })
        }
    }
}

@Composable
fun OptionsItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }
        .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, "")
        Spacer(Modifier.width(8.dp))
        Text(label)
    }
}

@Composable
fun OptionsHeader(
    name: String, exerciseNumber: Int, workoutType: WorkoutType? = null, date: String?
) {
    Column(
        Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
    ) {
        Text(
            name, style = MaterialTheme.typography.labelMedium, fontSize = TextUnit(
                16f, TextUnitType.Sp
            )
        )
        Row {
            workoutType?.let {
                Text(it.value)
                Text("-", modifier = Modifier.padding(horizontal = 8.dp))
            }

            Text("$exerciseNumber ${stringResource(R.string.exercises)}")

            date?.let {
                Text("-", modifier = Modifier.padding(horizontal = 8.dp))
                Text(it)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OptionsScreenPreview() {
    OptionsScreen("Workout", "Abs", 8, WorkoutType.Gym, "12.12.2012", {}, {})
}

@Preview
@Composable
fun OptionsRowPreview() {
    Column {
        OptionsItem(Icons.Default.Delete, "Delete") {}
        OptionsItem(Icons.Default.Edit, "Delete") {}
    }
}
