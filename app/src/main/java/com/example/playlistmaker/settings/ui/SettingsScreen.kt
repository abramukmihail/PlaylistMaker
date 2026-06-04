package com.example.playlistmaker.settings.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import com.example.playlistmaker.sharing.domain.interactor.SharingInteractor
import com.example.playlistmaker.ui.components.TopBar
import com.example.playlistmaker.ui.theme.PlaylistMakerTheme

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val isDarkTheme by viewModel.themeState.observeAsState(initial = false)

    PlaylistMakerTheme(darkTheme = isDarkTheme) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            TopBar(title = stringResource(R.string.settings))

            Spacer(modifier = Modifier.height(24.dp))

            SettingsSwitchRow(
                title = stringResource(R.string.black_team),
                checked = isDarkTheme,
                onCheckedChange = { viewModel.toggleDarkTheme(it) }
            )

            SettingsIconRow(
                title = stringResource(R.string.share_app),
                iconRes = R.drawable.ic_send_black_24,
                onClick = { viewModel.shareApp() }
            )

            SettingsIconRow(
                title = stringResource(R.string.write_to_support),
                iconRes = R.drawable.ic_support_24,
                onClick = { viewModel.contactSupport() }
            )

            SettingsIconRow(
                title = stringResource(R.string.user_agreement),
                iconRes = R.drawable.ic_user_agreement_24,
                onClick = { viewModel.transitionAgreement() }
            )
        }
    }
}

@Composable
fun SettingsSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(61.dp)
            .padding(start = 16.dp, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        CustomSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun CustomSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val thumbPainter = painterResource(R.drawable.switch_thumb)
    val trackPainter = painterResource(R.drawable.switch_track)

    val thumbColorChecked = Color(0xFF3772E7)
    val thumbColorUnchecked = Color(0xFFAEAFB4)
    val trackColorChecked = Color(0xFF9FBBF3)
    val trackColorUnchecked = Color(0xFFE6E8EB)

    val thumbColor = if (checked) thumbColorChecked else thumbColorUnchecked
    val trackColor = if (checked) trackColorChecked else trackColorUnchecked

    val switchWidth = 56.dp
    val switchHeight = 40.dp
    val offsetX by animateDpAsState(
        targetValue = if (checked) 20.dp else 0.dp,
        animationSpec = tween(durationMillis = 200),
        label = "switch_offset"
    )

    Box(
        modifier = modifier
            .width(switchWidth)
            .height(switchHeight)
            .clickable { onCheckedChange(!checked) }
    ) {
        Image(
            painter = trackPainter,
            colorFilter = ColorFilter.tint(trackColor),
            contentDescription = null,
            modifier = Modifier.align(Alignment.Center)
        )

        Box(
            modifier = Modifier
                .width(switchWidth)
                .height(switchHeight)
                .offset { IntOffset(x = offsetX.roundToPx(), y = 0) }
        ) {
            Image(
                painter = thumbPainter,
                contentDescription = null,
                colorFilter = ColorFilter.tint(thumbColor),
                modifier = Modifier.padding(start = 9.dp, top = 11.dp)
            )
        }
    }
}

@Composable
fun SettingsIconRow(
    title: String,
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(61.dp)
            .clickable { onClick() }
            .padding(start = 16.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = title,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, name = "Light theme")
@Composable
fun PreviewSettingsScreenLight() {
    SettingsScreen(viewModel = createFakeViewModel())
}

private fun createFakeViewModel(): SettingsViewModel {
    val fakeSettingsInteractor = object : SettingsInteractor {
        override fun getThemeSetting(): Boolean = false
        override fun updateThemeSetting(darkTheme: Boolean) {}
        override fun applySavedTheme() {}
    }
    val fakeSharingInteractor = object : SharingInteractor {
        override fun shareApp() {}
        override fun contactSupport() {}
        override fun transitionAgreement() {}
    }
    return SettingsViewModel(fakeSettingsInteractor, fakeSharingInteractor)
}