package com.example.cutestage

import android.content.Context

/**
 * Character resource names organized by gender and action/emotion
 * Use these strings to dynamically load drawable resources at runtime
 */
object CharacterResources {

    /**
     * Female character resource names (28 resources)
     * Includes: idle, walking, emotions, speaking, singing, dancing, clapping
     */
    val FEMALE_CHARACTER_RESOURCES = arrayOf(
        "stage_female_1_idle_1",
        "stage_female_1_idle_2",
        "stage_female_1_walking_1",
        "stage_female_1_walking_2",
        "stage_female_1_annoyed_1",
        "stage_female_1_annoyed_2",
        "stage_female_1_idle_annoyed_1",
        "stage_female_1_idle_annoyed_2",
        "stage_female_1_listening_1",
        "stage_female_1_listening_2",
        "stage_female_1_speak_normal_1",
        "stage_female_1_speak_normal_2",
        "stage_female_1_speak_angry_1",
        "stage_female_1_speak_angry_2",
        "stage_female_1_sing_nomal_1",
        "stage_female_1_sing_nomal_2",
        "stage_female_1_sing_climax_1",
        "stage_female_1_sing_climax_2",
        "stage_female_1_sing_pitchup_1",
        "stage_female_1_sing_pitchup_2",
        "stage_female_1_dancing_type_a_1",
        "stage_female_1_dancing_type_a_2",
        "stage_female_1_dancing_type_b_1",
        "stage_female_1_dancing_type_b_2",
        "stage_female_1_dancing_type_c_1",
        "stage_female_1_dancing_type_c_2",
        "stage_female_1_clap_1",
        "stage_female_1_clap_2"
    )

    /**
     * Male character resource names (28 resources)
     * Includes: idle, waking, emotions, speaking, singing, dancing, clapping
     */
    val MALE_CHARACTER_RESOURCES = arrayOf(
        "stage_male_1_idle_1",
        "stage_male_1_idle_2",
        "stage_male_1_waking_1",
        "stage_male_1_waking_2",
        "stage_male_1_annoyed_1",
        "stage_male_1_annoyed_2",
        "stage_male_1_idle_annoyed_1",
        "stage_male_1_idle_annoyed_2",
        "stage_male_1_listening_1",
        "stage_male_1_listening_2",
        "stage_male_1_speak_normal_1",
        "stage_male_1_speak_normal_2",
        "stage_male_1_speak_angry_1",
        "stage_male_1_speak_angry_2",
        "stage_male_1_sing_nomal_1",
        "stage_male_1_sing_nomal_2",
        "stage_male_1_sing_climax_1",
        "stage_male_1_sing_climax_2",
        "stage_male_1_sing_pitchup_1",
        "stage_male_1_sing_pitchup_2",
        "stage_male_1_dancing_type_a_1",
        "stage_male_1_dancing_type_a_2",
        "stage_male_1_dancing_type_b_1",
        "stage_male_1_dancing_type_b_2",
        "stage_male_1_dancing_type_c_1",
        "stage_male_1_dancing_type_c_2",
        "stage_male_1_clap_1",
        "stage_male_1_clap_2"
    )

    /**
     * All character resource names combined (56 resources)
     */
    val ALL_CHARACTER_RESOURCES = FEMALE_CHARACTER_RESOURCES + MALE_CHARACTER_RESOURCES

    /**
     * Get drawable resource ID from resource name
     * @param context Android context
     * @param resourceName Resource name (e.g., "stage_female_1_idle_1")
     * @return Drawable resource ID, or 0 if not found
     */
    fun getResourceId(context: Context, resourceName: String): Int {
        return context.resources.getIdentifier(resourceName, "drawable", context.packageName)
    }

    /**
     * Get drawable from resource name
     * @param context Android context
     * @param resourceName Resource name (e.g., "stage_female_1_idle_1")
     * @return Drawable object or null if not found
     */
    fun getDrawable(context: Context, resourceName: String): android.graphics.drawable.Drawable? {
        val resourceId = getResourceId(context, resourceName)
        return if (resourceId != 0) {
            androidx.core.content.ContextCompat.getDrawable(context, resourceId)
        } else {
            null
        }
    }
}
