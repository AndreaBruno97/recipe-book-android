package com.example.recipebook.ui.preview

import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "phone", device = "spec:width=411dp,height=891dp")
annotation class PhonePreview

@Preview(name = "phone", device = "spec:width=673dp,height=841dp")
annotation class FoldablePreview

@Preview(name = "tablet", device = "spec:width=1280dp,height=800dp,dpi=240")
annotation class TabletPreview

@Preview(showBackground = true)
annotation class DefaultPreview