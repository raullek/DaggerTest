package az.less.entry

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding
import az.less.core.di.api
import az.less.feature.profile.api.ProfileFeatureApi

/**
 * UI-хост. Никакой бизнес-логики — только запуск стартовой фичи через её лаунчер + включение
 * edge-to-edge (контент рисуется под системными барами, а insets отдаются как паддинги контенту).
 *
 * Edge-to-edge включается здесь централизованно: фичи/SDUI-хост ничего про системные бары не знают —
 * паддинг безопасной зоны вешается на общий контейнер `android.R.id.content`, под которым живут все
 * фрагменты.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        applySystemBarInsets()

        if (savedInstanceState == null) {
            api<ProfileFeatureApi>().launcher().launch(supportFragmentManager)
        }
    }

    /** Рисуем под системными барами + прозрачные бары + контрастные (тёмные) иконки для светлой темы. */
    private fun enableEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isStatusBarContrastEnforced = false
            window.isNavigationBarContrastEnforced = false
        }
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
    }

    /** Паддинг безопасной зоны (системные бары + вырез) на общий контейнер фрагментов. */
    private fun applySystemBarInsets() {
        val content = findViewById<View>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(content) { view, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout(),
            )
            view.updatePadding(bars.left, bars.top, bars.right, bars.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }
}
