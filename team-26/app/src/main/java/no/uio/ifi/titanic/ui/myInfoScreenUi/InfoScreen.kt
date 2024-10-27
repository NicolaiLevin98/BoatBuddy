package no.uio.ifi.titanic.ui.myInfoScreenUi


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import no.uio.ifi.titanic.DataStoreViewModel
import no.uio.ifi.titanic.R
import no.uio.ifi.titanic.ui.navigationComponents.BottombarDesign
import no.uio.ifi.titanic.ui.theme.TitanicTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyInfoScreen(
    locationViewModel: LocationViewModel = viewModel(),
    navController: NavController,
    dataStoreViewModel: DataStoreViewModel
) {
    val scope = rememberCoroutineScope()
    val darktheme = dataStoreViewModel.darkModeFlow.collectAsState(initial = false).value
    TitanicTheme(
        darkTheme = darktheme
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        scrolledContainerColor = Color.Unspecified,
                        navigationIconContentColor = Color.Unspecified,
                        titleContentColor = Color.Unspecified,
                        actionIconContentColor = Color.Unspecified
                    ),
                    title = {
                        Box(
                            contentAlignment = Alignment.CenterEnd,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Mørk modus",
                                    modifier = Modifier.padding(end = 10.dp),
                                    fontWeight = FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 15.sp,
                                )
                                Button(
                                    colors = ButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                                        disabledContainerColor = Color.Transparent,
                                        disabledContentColor = Color.Transparent
                                    ),
                                    onClick = {
                                        scope.launch {
                                            dataStoreViewModel.toggleDarkMode()
                                        }
                                    }
                                ) {
                                    val text = if(darktheme) "skru av mørk modus" else "skru på mørkmodus"
                                    Text(text = text)
                                }
                                Spacer(modifier = Modifier.padding(3.dp))
                            }
                        }
                    }
                )
            },
            bottomBar = {
                BottomAppBar {
                    BottombarDesign(
                        navController,
                        "Info",
                        onHomeClick = {
                            navController.navigate("homeScreen") {
                                popUpTo("homeScreen") { inclusive = false }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onInfoClick = {}
                    )
                }
            }
        ) { contentPadding ->
            Column(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Spacer(modifier = Modifier.padding(30.dp))

                Text(
                    text = "BoatBuddy",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp
                )

                Spacer(modifier = Modifier.padding(10.dp))

                Image(
                    painter =  painterResource(id = R.drawable.nyinfosbilde),
                    contentDescription = "To personer som er ute går en tur ved vannet, " +
                            "med et fyrtårn i bakgrunn og en måke som flyr i bakgrunnen",
                    modifier = Modifier
                        .padding(20.dp)
                        .clip(RoundedCornerShape(200.dp))
                        .size(250.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.padding(10.dp))

                Text(
                    text = "Velkommen til din personlige værguide på havet!",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding( 25.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp
                )
                Text(
                    text = "Boatbuddy er en app for deg som ferdes mye på havet. " +
                            "Her får du oversikt over været der du er eller dit du ønsker å reise! " +
                            "All værdata er hentet fra Metrologisk institutt.",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(horizontal = 20.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Light,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.padding(40.dp))

                RSinfo(locationViewModel)
                Spacer(modifier = Modifier.padding(40.dp))
                Text(
                    text = "Hva tåler din båt?",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    textAlign = TextAlign.Left,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "CE-kategorisering er en sertifisering som vurderer " +
                            "hvor sjødyktig ditt fritidsfartøy er.",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    textAlign = TextAlign.Left,
                    color = MaterialTheme.colorScheme.onBackground
                )
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(categories) { category ->
                        InfoBoatCategory(categoryTitle = category.first, categoryDescription = category.second)
                    }
                }
            }
        }
    }
}