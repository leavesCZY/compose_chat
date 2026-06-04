package github.leavesczy.compose_chat.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.R
import github.leavesczy.compose_chat.theme.AppTheme

@Composable
fun LazyItemScope.EmptyPage(modifier: Modifier) {
    Box(
        modifier = modifier
            .animateItem()
            .fillParentMaxWidth()
            .fillParentMaxHeight(fraction = 0.85f),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier,
            text = stringResource(id = R.string.empty),
            fontSize = 68.sp,
            lineHeight = 70.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = AppTheme.colorScheme.c_FF001018_DEFFFFFF.color
        )
    }
}