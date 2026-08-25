package com.v2ray.ang.ui.main

import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.v2ray.ang.R
import com.v2ray.ang.dto.entities.ProfileItem
import com.v2ray.ang.enums.EConfigType
import com.v2ray.ang.extension.isComplexType
import com.v2ray.ang.ui.compose.AppDropdownMenuItems
import com.v2ray.ang.ui.compose.SelectListDialog

private enum class ImportMenuAction(
    @StringRes val labelRes: Int,
    val action: MainAction,
    val monogram: String,
    val category: MenuAccentCategory
) {
    QRCode(R.string.menu_item_import_config_qrcode, MainAction.ImportQRcode, "QR", MenuAccentCategory.Import),
    Clipboard(R.string.menu_item_import_config_clipboard, MainAction.ImportClipboard, "CB", MenuAccentCategory.Import),
    LocalFile(R.string.menu_item_import_config_local, MainAction.ImportConfigLocal, "F", MenuAccentCategory.Import),
    PolicyGroup(R.string.menu_item_import_config_policy_group, MainAction.ImportManually(EConfigType.POLICYGROUP.value), "PG", MenuAccentCategory.Group),
    ProxyChain(R.string.menu_item_import_config_proxy_chain, MainAction.ImportManually(EConfigType.PROXYCHAIN.value), "PC", MenuAccentCategory.Group),
    Vmess(R.string.menu_item_import_config_manually_vmess, MainAction.ImportManually(EConfigType.VMESS.value), "V", MenuAccentCategory.Protocol),
    Vless(R.string.menu_item_import_config_manually_vless, MainAction.ImportManually(EConfigType.VLESS.value), "VL", MenuAccentCategory.Protocol),
    Shadowsocks(R.string.menu_item_import_config_manually_ss, MainAction.ImportManually(EConfigType.SHADOWSOCKS.value), "SS", MenuAccentCategory.Protocol),
    Socks(R.string.menu_item_import_config_manually_socks, MainAction.ImportManually(EConfigType.SOCKS.value), "SK", MenuAccentCategory.Protocol),
    Http(R.string.menu_item_import_config_manually_http, MainAction.ImportManually(EConfigType.HTTP.value), "H", MenuAccentCategory.Protocol),
    Trojan(R.string.menu_item_import_config_manually_trojan, MainAction.ImportManually(EConfigType.TROJAN.value), "T", MenuAccentCategory.Protocol),
    WireGuard(R.string.menu_item_import_config_manually_wireguard, MainAction.ImportManually(EConfigType.WIREGUARD.value), "WG", MenuAccentCategory.Protocol),
    Hysteria2(R.string.menu_item_import_config_manually_hysteria2, MainAction.ImportManually(EConfigType.HYSTERIA2.value), "H2", MenuAccentCategory.Protocol)
}

private enum class MenuAccentCategory { Import, Group, Protocol, Destructive, Test, Neutral }

enum class MainMoreMenuAction(
    @StringRes val labelRes: Int,
    val monogram: String,
    private val category: MenuAccentCategory
) {
    RestartService(R.string.title_service_restart, "R", MenuAccentCategory.Neutral),
    DeleteAll(R.string.title_del_all_config, "D", MenuAccentCategory.Destructive),
    DeleteDuplicate(R.string.title_del_duplicate_config, "D", MenuAccentCategory.Destructive),
    DeleteInvalid(R.string.title_del_invalid_config, "D", MenuAccentCategory.Destructive),
    ExportAll(R.string.title_export_all, "E", MenuAccentCategory.Neutral),
    LocateSelected(R.string.title_locate_selected_config, "L", MenuAccentCategory.Test),
    SortByTestResults(R.string.title_sort_by_test_results, "S", MenuAccentCategory.Test),
    TestAll(R.string.title_ping_all_server, "T", MenuAccentCategory.Test),
    TestAllRealPing(R.string.title_real_ping_all_server, "T", MenuAccentCategory.Test),
    UpdateSubscriptions(R.string.title_sub_update, "U", MenuAccentCategory.Neutral);

    internal fun accentCategory() = category
}

@Composable
private fun MenuAccentCategory.toColor(): Color = when (this) {
    MenuAccentCategory.Import -> MaterialTheme.colorScheme.tertiary
    MenuAccentCategory.Group -> MaterialTheme.colorScheme.secondary
    MenuAccentCategory.Protocol -> MaterialTheme.colorScheme.secondary
    MenuAccentCategory.Destructive -> MaterialTheme.colorScheme.primary
    MenuAccentCategory.Test -> MaterialTheme.colorScheme.tertiary
    MenuAccentCategory.Neutral -> MaterialTheme.colorScheme.onSurfaceVariant
}

internal enum class ServerMenuAction(
    @StringRes val labelRes: Int,
    val isShareAction: Boolean,
    val supportsComplexProfiles: Boolean,
) {
    ShareQRCode(R.string.share_method_qrcode, isShareAction = true, supportsComplexProfiles = false),
    ShareClipboard(R.string.share_method_clipboard, isShareAction = true, supportsComplexProfiles = false),
    ShareFullContent(R.string.share_method_full_content, isShareAction = true, supportsComplexProfiles = true),
    Edit(R.string.action_edit, isShareAction = false, supportsComplexProfiles = true),
    Delete(R.string.action_delete, isShareAction = false, supportsComplexProfiles = true),
}

internal fun serverMenuActions(
    isComplexProfile: Boolean,
    includeManagementActions: Boolean,
): List<ServerMenuAction> = ServerMenuAction.entries.filter { action ->
    (includeManagementActions || action.isShareAction) && (!isComplexProfile || action.supportsComplexProfiles)
}

@Composable
fun ImportMenuContent(onAction: (MainAction) -> Unit) = AppDropdownMenuItems(
    items = ImportMenuAction.entries,
    labelRes = { it.labelRes },
    onSelected = { onAction(it.action) },
    monogram = { it.monogram },
    accentColor = { it.category.toColor() }
)

@Composable
fun MoreMenuContent(onSelected: (MainMoreMenuAction) -> Unit) = AppDropdownMenuItems(
    items = MainMoreMenuAction.entries,
    labelRes = { it.labelRes },
    onSelected = onSelected,
    monogram = { it.monogram },
    accentColor = { it.accentCategory().toColor() }
)

@Composable
fun ShareMethodDialog(
    guid: String,
    profile: ProfileItem,
    more: Boolean,
    onDismiss: () -> Unit,
    onAction: (MainAction) -> Unit,
    onRemove: (String) -> Unit,
) {
    val menuActions = serverMenuActions(
        isComplexProfile = profile.configType.isComplexType(),
        includeManagementActions = more,
    )
    SelectListDialog(
        options = menuActions,
        optionText = { stringResource(it.labelRes) },
        onSelected = { action ->
            onDismiss()
            when (action) {
                ServerMenuAction.ShareQRCode -> onAction(MainAction.ShareQRCode(guid))
                ServerMenuAction.ShareClipboard -> onAction(MainAction.ShareClipboard(guid))
                ServerMenuAction.ShareFullContent -> onAction(MainAction.ShareFullContent(guid))
                ServerMenuAction.Edit -> onAction(MainAction.EditServer(guid, profile))
                ServerMenuAction.Delete -> onRemove(guid)
            }
        },
        onDismiss = onDismiss
    )
}
