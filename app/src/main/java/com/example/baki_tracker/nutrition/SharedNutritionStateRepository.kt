package com.example.baki_tracker.nutrition

import com.example.baki_tracker.dependencyInjection.Singleton
import com.example.baki_tracker.model.nutrition.NutritionTrackingDay
import com.example.baki_tracker.workout.components.DialogInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.tatarka.inject.annotations.Inject

@Inject
@Singleton
class SharedNutritionStateRepository : ISharedNutritionStateRepository {
    private val _selectedTrackingDay: MutableStateFlow<NutritionTrackingDay?> =
        MutableStateFlow(null)
    override val selectedTrackingDay = _selectedTrackingDay.asStateFlow()

    private val _selectedBottomSheet: MutableStateFlow<NutritionBottomSheet> =
        MutableStateFlow(NutritionBottomSheet.NONE)
    override val selectedBottomSheet = _selectedBottomSheet.asStateFlow()

    private val _dialog: MutableStateFlow<DialogInfo?> = MutableStateFlow(null)
    override val dialog = _dialog.asStateFlow()

    override fun updateSelectedTrackingDay(nutritionTrackingDay: NutritionTrackingDay?) {
        _selectedTrackingDay.value = nutritionTrackingDay
    }


    override fun updateSelectedBottomSheet(bottomSheet: NutritionBottomSheet) {
        _selectedBottomSheet.value = bottomSheet
    }

    override fun updateDialog(dialogInfo: DialogInfo?) {
        _dialog.value = dialogInfo
    }

    override fun dismissBottomSheet() {
        _selectedBottomSheet.value = NutritionBottomSheet.NONE
    }
}

interface ISharedNutritionStateRepository {
    val selectedTrackingDay: StateFlow<NutritionTrackingDay?>
    val selectedBottomSheet: StateFlow<NutritionBottomSheet>
    val dialog: StateFlow<DialogInfo?>

    fun updateSelectedTrackingDay(nutritionTrackingDay: NutritionTrackingDay?)
    fun updateSelectedBottomSheet(bottomSheet: NutritionBottomSheet)
    fun updateDialog(dialogInfo: DialogInfo?)
    fun dismissBottomSheet()
}

enum class NutritionBottomSheet {
    NONE,DETAILS
}