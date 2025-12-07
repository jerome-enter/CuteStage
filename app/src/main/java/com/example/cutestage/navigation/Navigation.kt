package com.example.cutestage.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cutestage.stage.StageScreen
import com.example.cutestage.stage.StageTestScenario
import com.example.cutestage.ui.creator.ScenarioCreatorScreen
import com.example.cutestage.ui.player.PlayerScreen
import com.example.cutestage.ui.scenariolist.ScenarioListScreen

/**
 * 앱 전체 네비게이션 구조
 */
sealed class Screen(val route: String) {
    object Stage : Screen("stage")
    object ScenarioList : Screen("scenario_list")
    object ScenarioCreator : Screen("scenario_creator?scenarioId={scenarioId}") {
        fun createRoute(scenarioId: String? = null): String {
            return if (scenarioId != null) {
                "scenario_creator?scenarioId=$scenarioId"
            } else {
                "scenario_creator"
            }
        }
    }

    object Player : Screen("player/{scenarioId}") {
        fun createRoute(scenarioId: String): String = "player/$scenarioId"
    }
}

@Composable
fun CuteStageNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Stage.route
    ) {
        // 메인 무대 화면
        composable(Screen.Stage.route) {
            StageScreen(
                onScenarioSelectClick = {
                    navController.navigate(Screen.ScenarioList.route)
                }
            )
        }

        // 시나리오 목록 화면
        composable(Screen.ScenarioList.route) {
            ScenarioListScreen(
                onScenarioClick = { scenarioId ->
                    navController.navigate(Screen.Player.createRoute(scenarioId))
                },
                onEditClick = { scenarioId ->
                    navController.navigate(Screen.ScenarioCreator.createRoute(scenarioId))
                },
                onCreateNew = {
                    navController.navigate(Screen.ScenarioCreator.createRoute())
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 시나리오 생성/편집 화면
        composable(
            route = "scenario_creator?scenarioId={scenarioId}",
            arguments = listOf(
                navArgument("scenarioId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            ScenarioCreatorScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 시나리오 재생 화면
        composable(
            route = "player/{scenarioId}",
            arguments = listOf(
                navArgument("scenarioId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val scenarioId = backStackEntry.arguments?.getString("scenarioId")

            // 템플릿 시나리오면 기존 StageScreen 사용 (콜백 포함)
            when (scenarioId) {
                "template_playground" -> {
                    StageTestScenario.currentScenario = StageTestScenario.ScenarioType.PLAYGROUND
                    StageScreen(
                        onScenarioSelectClick = {
                            navController.navigate(Screen.ScenarioList.route)
                        }
                    )
                }

                "template_basic" -> {
                    StageTestScenario.currentScenario = StageTestScenario.ScenarioType.BASIC
                    StageScreen(
                        onScenarioSelectClick = {
                            navController.navigate(Screen.ScenarioList.route)
                        }
                    )
                }

                "template_couple_fight" -> {
                    StageTestScenario.currentScenario = StageTestScenario.ScenarioType.COUPLE_FIGHT
                    StageScreen(
                        onScenarioSelectClick = {
                            navController.navigate(Screen.ScenarioList.route)
                        }
                    )
                }

                "template_oksun" -> {
                    StageTestScenario.currentScenario =
                        StageTestScenario.ScenarioType.OKSUN_MONOLOGUE
                    StageScreen(
                        onScenarioSelectClick = {
                            navController.navigate(Screen.ScenarioList.route)
                        }
                    )
                }

                "template_i_am_solo" -> {
                    StageTestScenario.currentScenario = StageTestScenario.ScenarioType.I_AM_SOLO
                    StageScreen(
                        onScenarioSelectClick = {
                            navController.navigate(Screen.ScenarioList.route)
                        }
                    )
                }

                "template_foolish_trick" -> {
                    StageTestScenario.currentScenario = StageTestScenario.ScenarioType.FOOLISH_TRICK
                    StageScreen(
                        onScenarioSelectClick = {
                            navController.navigate(Screen.ScenarioList.route)
                        }
                    )
                }

                "template_song" -> {
                    StageTestScenario.currentScenario = StageTestScenario.ScenarioType.SONG
                    StageScreen(
                        onScenarioSelectClick = {
                            navController.navigate(Screen.ScenarioList.route)
                        }
                    )
                }

                else -> {
                    // 사용자 생성 시나리오는 PlayerScreen 사용
                    PlayerScreen()
                }
            }
        }
    }
}
