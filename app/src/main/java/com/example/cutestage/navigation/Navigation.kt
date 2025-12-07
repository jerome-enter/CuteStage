package com.example.cutestage.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cutestage.stage.StageScreen
import com.example.cutestage.stage.StageTestScenario
import com.example.cutestage.ui.creator.ScenarioCreatorScreen
import com.example.cutestage.ui.player.PlayerViewModel
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
                    // ScenarioList로 이동하되, Stage 위에만 쌓이도록
                    navController.navigate(Screen.ScenarioList.route) {
                        // Stage 하나만 백스택에 유지
                        popUpTo(Screen.Stage.route) {
                            inclusive = false
                        }
                        // 같은 화면 중복 방지
                        launchSingleTop = true
                    }
                }
            )
        }

        // 시나리오 목록 화면
        composable(Screen.ScenarioList.route) {
            ScenarioListScreen(
                onScenarioClick = { scenarioId ->
                    // Player로 이동하되, ScenarioList를 스택에서 제거
                    navController.navigate(Screen.Player.createRoute(scenarioId)) {
                        // Stage만 남기고 ScenarioList 제거
                        popUpTo(Screen.Stage.route) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
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
                            // ScenarioList로 이동하되, Player를 스택에서 제거
                            navController.navigate(Screen.ScenarioList.route) {
                                popUpTo(Screen.Stage.route) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                "template_basic" -> {
                    StageTestScenario.currentScenario = StageTestScenario.ScenarioType.BASIC
                    StageScreen(
                        onScenarioSelectClick = {
                            navController.navigate(Screen.ScenarioList.route) {
                                popUpTo(Screen.Stage.route) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                "template_couple_fight" -> {
                    StageTestScenario.currentScenario = StageTestScenario.ScenarioType.COUPLE_FIGHT
                    StageScreen(
                        onScenarioSelectClick = {
                            navController.navigate(Screen.ScenarioList.route) {
                                popUpTo(Screen.Stage.route) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                "template_oksun" -> {
                    StageTestScenario.currentScenario =
                        StageTestScenario.ScenarioType.OKSUN_MONOLOGUE
                    StageScreen(
                        onScenarioSelectClick = {
                            navController.navigate(Screen.ScenarioList.route) {
                                popUpTo(Screen.Stage.route) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                "template_i_am_solo" -> {
                    StageTestScenario.currentScenario = StageTestScenario.ScenarioType.I_AM_SOLO
                    StageScreen(
                        onScenarioSelectClick = {
                            navController.navigate(Screen.ScenarioList.route) {
                                popUpTo(Screen.Stage.route) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                "template_foolish_trick" -> {
                    StageTestScenario.currentScenario = StageTestScenario.ScenarioType.FOOLISH_TRICK
                    StageScreen(
                        onScenarioSelectClick = {
                            navController.navigate(Screen.ScenarioList.route) {
                                popUpTo(Screen.Stage.route) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                "template_song" -> {
                    StageTestScenario.currentScenario = StageTestScenario.ScenarioType.SONG
                    StageScreen(
                        onScenarioSelectClick = {
                            navController.navigate(Screen.ScenarioList.route) {
                                popUpTo(Screen.Stage.route) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                else -> {
                    // 사용자 생성 시나리오도 StageScreen 사용
                    val viewModel: PlayerViewModel = hiltViewModel()
                    val state = viewModel.state

                    Box(modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)) {
                        when {
                            state.isLoading -> {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    CircularProgressIndicator(color = Color.White)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text("시나리오 로딩 중...", color = Color.White)
                                }
                            }

                            state.error != null -> {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text("❌", style = MaterialTheme.typography.displayLarge)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        state.error,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }

                            state.script != null -> {
                                // ✅ StageScreen 사용 (템플릿과 완전히 동일)
                                key(state.script) {
                                    StageScreen(
                                        script = state.script,
                                        onScenarioSelectClick = {
                                            navController.navigate(Screen.ScenarioList.route) {
                                                popUpTo(Screen.Stage.route) {
                                                    inclusive = false
                                                }
                                                launchSingleTop = true
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
