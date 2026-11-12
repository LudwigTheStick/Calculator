package com.example.calculator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.R
import com.example.calculator.domain.BinaryOperator
import com.example.calculator.domain.HistoryItem
import com.example.calculator.domain.UnaryOperator
import com.example.calculator.ui.theme.WinDarkBg
import com.example.calculator.ui.theme.WinDarkBorder
import com.example.calculator.ui.theme.WinDarkKeyAccent
import com.example.calculator.ui.theme.WinDarkKeyNumber
import com.example.calculator.ui.theme.WinDarkKeyOperator
import com.example.calculator.ui.theme.WinDarkSurface
import com.example.calculator.ui.theme.WinDarkTextPrimary
import com.example.calculator.ui.theme.WinDarkTextSecondary
import com.example.calculator.ui.theme.WinLightBg
import com.example.calculator.ui.theme.WinLightBorder
import com.example.calculator.ui.theme.WinLightKeyAccent
import com.example.calculator.ui.theme.WinLightKeyNumber
import com.example.calculator.ui.theme.WinLightKeyOperator
import com.example.calculator.ui.theme.WinLightSurface
import com.example.calculator.ui.theme.WinLightTextPrimary
import com.example.calculator.ui.theme.WinLightTextSecondary

enum class ButtonType {
    NUMBER,
    OPERATOR,
    ACCENT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDark = isSystemInDarkTheme()

    val bgColor = if (isDark) WinDarkBg else WinLightBg
    val textColor = if (isDark) WinDarkTextPrimary else WinLightTextPrimary
    val secondaryTextColor = if (isDark) WinDarkTextSecondary else WinLightTextSecondary

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp, vertical = 8.dp)
        ) {
            // Header: "Standard" mode title + History toggle button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = null,
                            tint = textColor
                        )
                    }
                    Text(
                        text = stringResource(R.string.calculator_mode_standard),
                        color = textColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                IconButton(
                    onClick = { viewModel.onToggleHistory() },
                    modifier = Modifier.semantics {
                        contentDescription = "Open History"
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = stringResource(R.string.history_title),
                        tint = textColor
                    )
                }
            }

            // Display Section: Secondary formula & Primary main number
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.9f)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                // Secondary display (Expression)
                Text(
                    text = uiState.secondaryDisplay,
                    color = secondaryTextColor,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Primary display (Current operand / Result / Error)
                val primaryText = if (uiState.hasError) {
                    when (uiState.errorStringRes) {
                        "error_divide_by_zero" -> stringResource(R.string.error_divide_by_zero)
                        else -> stringResource(R.string.error_invalid_input)
                    }
                } else {
                    // Display localized decimal separator if needed
                    val decSep = stringResource(R.string.btn_decimal)
                    uiState.primaryDisplay.replace(".", decSep)
                }

                val primaryFontSize = when {
                    primaryText.length > 14 -> 30.sp
                    primaryText.length > 10 -> 38.sp
                    else -> 48.sp
                }

                Text(
                    text = primaryText,
                    color = textColor,
                    fontSize = primaryFontSize,
                    fontWeight = FontWeight.Light,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Keypad Grid (6 rows x 4 columns)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2.4f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Row 1: %, CE, C, ⌫
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    CalcKey(
                        text = stringResource(R.string.btn_percent),
                        contentDesc = stringResource(R.string.cd_percent),
                        type = ButtonType.OPERATOR,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.onPercent() }
                    )
                    CalcKey(
                        text = stringResource(R.string.btn_clear_entry),
                        contentDesc = stringResource(R.string.cd_clear_entry),
                        type = ButtonType.OPERATOR,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.onClearEntry() }
                    )
                    CalcKey(
                        text = stringResource(R.string.btn_clear),
                        contentDesc = stringResource(R.string.cd_clear),
                        type = ButtonType.OPERATOR,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.onClear() }
                    )
                    CalcKey(
                        text = stringResource(R.string.btn_backspace),
                        contentDesc = stringResource(R.string.cd_backspace),
                        type = ButtonType.OPERATOR,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.onBackspace() }
                    )
                }

                // Row 2: 1/x, x², √x, ÷
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    CalcKey(
                        text = stringResource(R.string.btn_reciprocal),
                        contentDesc = stringResource(R.string.cd_reciprocal),
                        type = ButtonType.OPERATOR,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.onUnary(UnaryOperator.RECIPROCAL) }
                    )
                    CalcKey(
                        text = stringResource(R.string.btn_square),
                        contentDesc = stringResource(R.string.cd_square),
                        type = ButtonType.OPERATOR,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.onUnary(UnaryOperator.SQUARE) }
                    )
                    CalcKey(
                        text = stringResource(R.string.btn_sqrt),
                        contentDesc = stringResource(R.string.cd_sqrt),
                        type = ButtonType.OPERATOR,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.onUnary(UnaryOperator.SQRT) }
                    )
                    CalcKey(
                        text = stringResource(R.string.btn_divide),
                        contentDesc = stringResource(R.string.cd_divide),
                        type = ButtonType.OPERATOR,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.onBinaryOperator(BinaryOperator.DIVIDE) }
                    )
                }

                // Row 3: 7, 8, 9, ×
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    CalcKey(
                        text = stringResource(R.string.btn_7),
                        type = ButtonType.NUMBER,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.onDigit('7') }
                    )
                    CalcKey(
                        text = stringResource(R.string.btn_8),
                        type = ButtonType.NUMBER,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.onDigit('8') }
                    )
                    CalcKey(
                        text = stringResource(R.string.btn_9),
                        type = ButtonType.NUMBER,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.onDigit('9') }
                    )
                    CalcKey(
                        text = stringResource(R.string.btn_multiply),
                        contentDesc = stringResource(R.string.cd_multiply),
                        type = ButtonType.OPERATOR,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.onBinaryOperator(BinaryOperator.MULTIPLY) }
                    )
                }

                // Row 4: 4, 5, 6, −
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    CalcKey(
                        text = stringResource(R.string.btn_4),
                        type = ButtonType.NUMBER,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.onDigit('4') }
                    )
                    CalcKey(
                        text = stringResource(R.string.btn_5),
                        type = ButtonType.NUMBER,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.onDigit('5') }
                    )
                    CalcKey(
                        text = stringResource(R.string.btn_6),
                        type = ButtonType.NUMBER,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.onDigit('6') }
                    )
                    CalcKey(
                        text = stringResource(R.string.btn_subtract),
                        contentDesc = stringResource(R.string.cd_subtract),
                        type = ButtonType.OPERATOR,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.onBinaryOperator(BinaryOperator.SUBTRACT) }
                    )
                }

                // Row 5: 1, 2, 3, +
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    CalcKey(
                        text = stringResource(R.string.btn_1),
                        type = ButtonType.NUMBER,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.onDigit('1') }
                    )
                    CalcKey(
                        text = stringResource(R.string.btn_2),
                        type = ButtonType.NUMBER,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.onDigit('2') }
                    )
                    CalcKey(
                        text = stringResource(R.string.btn_3),
                        type = ButtonType.NUMBER,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.onDigit('3') }
                    )
                    CalcKey(
                        text = stringResource(R.string.btn_add),
                        contentDesc = stringResource(R.string.cd_add),
                        type = ButtonType.OPERATOR,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.onBinaryOperator(BinaryOperator.ADD) }
                    )
                }

                // Row 6: +/-, 0, ,, =
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    CalcKey(
                        text = stringResource(R.string.btn_negate),
                        contentDesc = stringResource(R.string.cd_negate),
                        type = ButtonType.NUMBER,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.onNegate() }
                    )
                    CalcKey(
                        text = stringResource(R.string.btn_0),
                        type = ButtonType.NUMBER,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.onDigit('0') }
                    )
                    CalcKey(
                        text = stringResource(R.string.btn_decimal),
                        contentDesc = stringResource(R.string.cd_decimal),
                        type = ButtonType.NUMBER,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.onDecimal() }
                    )
                    CalcKey(
                        text = stringResource(R.string.btn_equals),
                        contentDesc = stringResource(R.string.cd_equals),
                        type = ButtonType.ACCENT,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.onEquals() }
                    )
                }
            }
        }

        // History Bottom Sheet
        if (uiState.isHistoryOpen) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.onCloseHistory() },
                sheetState = sheetState,
                containerColor = if (isDark) WinDarkSurface else WinLightSurface
            ) {
                HistoryPanel(
                    history = uiState.history,
                    onItemClick = { viewModel.onRecallHistory(it) },
                    onClearClick = { viewModel.onClearHistory() }
                )
            }
        }
    }
}

@Composable
fun CalcKey(
    text: String,
    modifier: Modifier = Modifier,
    contentDesc: String? = null,
    type: ButtonType = ButtonType.NUMBER,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()

    val containerColor = when (type) {
        ButtonType.NUMBER -> if (isDark) WinDarkKeyNumber else WinLightKeyNumber
        ButtonType.OPERATOR -> if (isDark) WinDarkKeyOperator else WinLightKeyOperator
        ButtonType.ACCENT -> if (isDark) WinDarkKeyAccent else WinLightKeyAccent
    }

    val contentColor = when (type) {
        ButtonType.ACCENT -> Color.White
        else -> if (isDark) WinDarkTextPrimary else WinLightTextPrimary
    }

    val borderColor = if (isDark) WinDarkBorder else WinLightBorder
    val shape = RoundedCornerShape(4.dp)
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(shape)
            .background(containerColor)
            .border(width = 0.5.dp, color = borderColor, shape = shape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = contentColor.copy(alpha = 0.2f)),
                onClick = onClick
            )
            .semantics {
                if (contentDesc != null) {
                    this.contentDescription = contentDesc
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = contentColor,
            fontSize = if (type == ButtonType.NUMBER) 19.sp else 16.sp,
            fontWeight = if (type == ButtonType.NUMBER) FontWeight.Normal else FontWeight.Medium
        )
    }
}

@Composable
fun HistoryPanel(
    history: List<HistoryItem>,
    onItemClick: (HistoryItem) -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val textColor = if (isDark) WinDarkTextPrimary else WinLightTextPrimary
    val secondaryColor = if (isDark) WinDarkTextSecondary else WinLightTextSecondary
    val decSep = stringResource(R.string.btn_decimal)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.history_title),
                color = textColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )

            if (history.isNotEmpty()) {
                IconButton(onClick = onClearClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.history_clear),
                        tint = secondaryColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = secondaryColor.copy(alpha = 0.2f))
        Spacer(modifier = Modifier.height(12.dp))

        if (history.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.history_empty),
                    color = secondaryColor,
                    fontSize = 15.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(history, key = { it.id }) { item ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onItemClick(item) }
                            .padding(vertical = 8.dp, horizontal = 12.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = item.expression,
                            color = secondaryColor,
                            fontSize = 14.sp,
                            textAlign = TextAlign.End
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.result.replace(".", decSep),
                            color = textColor,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    }
}
