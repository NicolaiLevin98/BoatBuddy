package no.uio.ifi.titanic.ui.homeScreen.homeScreenComponents

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.titanic.R

@Composable
fun SearchBar(
    searchBarUsed: Boolean,
    onSearchCommit: (String, Context) -> Unit,
    onLocationButtonClick: (Context) -> Unit,
) {
    val context = LocalContext.current
    var searchText by remember { mutableStateOf("") }
    var isLocationSearched by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused = interactionSource.collectIsFocusedAsState().value
    var isSearchBarFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    // Modify these color schemes based on focus state
    val searchButtonColor =
        if (searchBarUsed) MaterialTheme.colorScheme.secondary
        else MaterialTheme.colorScheme.tertiaryContainer
    val searchButtonBorderColor =
        if (searchBarUsed) MaterialTheme.colorScheme.tertiaryContainer
        else MaterialTheme.colorScheme.tertiaryContainer

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = { focusManager.clearFocus() })
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = {
                    Text(
                        "Søk på destinasjon...",
                        fontSize = 14.sp,
                        color = if (isFocused) Color.LightGray else Color.Gray
                    )
                },
                modifier = Modifier
                    .onFocusChanged { focusState ->
                        isSearchBarFocused = focusState.isFocused
                    }
                    .weight(1f)
                    .height(54.dp)
                    .background(Color.Transparent),
                interactionSource = interactionSource,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                singleLine = true,
                keyboardActions = KeyboardActions(
                    onSearch = {
                        keyboardController?.hide()
                        if (searchText.isNotBlank()) {
                            onSearchCommit(searchText, context)
                            isLocationSearched = true
                            focusManager.clearFocus()
                        }
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedBorderColor = Color.LightGray,
                    cursorColor = MaterialTheme.colorScheme.onPrimary,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                )
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .width(66.dp)
                    .height(54.dp)
                    .shadow(4.dp, shape = RoundedCornerShape(8.dp))
                    .border(1.2.dp, searchButtonBorderColor, shape = RoundedCornerShape(8.dp))
                    .background(searchButtonColor)
            ) {
                Button(
                    onClick = {
                        onLocationButtonClick(context)
                        searchText = ""
                        isLocationSearched = false
                        focusManager.clearFocus()
                    },
                    modifier = Modifier.fillMaxSize(),
                    colors = ButtonDefaults.buttonColors(Color.Transparent)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.my_location),
                        contentDescription = "Locate me",
                        tint = MaterialTheme.colorScheme.background,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}