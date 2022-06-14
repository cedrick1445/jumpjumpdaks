package com.dakulangsakalam.customwebview.presentation.ui.jump

enum class JumpType {
    JUMP_TESTING, // ID: 123456 - Jump into webview
    JUMP_LINK, // ID: Package Name - Jump into webview
    SILENT_LINK, // ID: Package Name - will not jump and will only get the URL
    SILENT_TESTING // ID: 123456 - will not jump and will only get the URL
}