package az.less.feature.profile.impl.data

import az.less.feature.profile.api.Profile
import az.less.feature.profile.api.ProfileRepository
import javax.inject.Inject

/**
 * Реализация доменного контракта на Retrofit. [ProfileService] построен из
 * Retrofit, который пришёл из core-network через граф приложения.
 *
 * Бэкенда в демо нет, поэтому при сетевой ошибке отдаём заглушку — так виден
 * весь путь инъекции, а экран остаётся рабочим оффлайн.
 */
internal class ProfileRepositoryImpl @Inject constructor(
    private val service: ProfileService,
) : ProfileRepository {

    override suspend fun getProfile(): Profile = try {
        val dto = service.fetchProfile()
        Profile(name = dto.name.orEmpty(), email = dto.email.orEmpty())
    } catch (e: Exception) {
        Profile(name = "Мир Рашад (оффлайн-заглушка)", email = "mirrashadhasanov@gmail.com")
    }
}
