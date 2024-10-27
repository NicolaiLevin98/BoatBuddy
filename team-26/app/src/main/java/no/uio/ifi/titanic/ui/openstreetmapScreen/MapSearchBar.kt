package no.uio.ifi.titanic.ui.openstreetmapScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MapSearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onSearchCommit: () -> Unit,
    modifier: Modifier = Modifier,
    borderColor: Color = Color(0xFF84B1BA)
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused = interactionSource.collectIsFocusedAsState().value

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = if (isFocused) borderColor else Color.LightGray,
                shape = RoundedCornerShape(8.dp)
            )
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            singleLine = true,
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 14.sp
            ),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                    onSearchCommit()
                    focusManager.clearFocus()
                }
            ),
            interactionSource = interactionSource,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) { innerTextField ->
            Box(
                Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (searchText.isEmpty()) {
                    Text(
                        text = "Søk på destinasjon...",
                        fontSize = 14.sp,
                        color = if (isFocused) Color.LightGray else Color.Gray
                    )
                }
                innerTextField()
            }
        }
    }
}