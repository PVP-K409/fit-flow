package com.github.k409.fitflow.model

import androidx.annotation.StringRes
import com.github.k409.fitflow.R

const val EXERCISE_TYPE_BADMINTON = 2
const val EXERCISE_TYPE_BASEBALL = 4
const val EXERCISE_TYPE_BASKETBALL = 5
const val EXERCISE_TYPE_BIKING = 8
const val EXERCISE_TYPE_BIKING_STATIONARY = 9
const val EXERCISE_TYPE_BOOT_CAMP = 10
const val EXERCISE_TYPE_BOXING = 11
const val EXERCISE_TYPE_CALISTHENICS = 13
const val EXERCISE_TYPE_CRICKET = 14
const val EXERCISE_TYPE_DANCING = 16
const val EXERCISE_TYPE_ELLIPTICAL = 25
const val EXERCISE_TYPE_EXERCISE_CLASS = 26
const val EXERCISE_TYPE_FENCING = 27
const val EXERCISE_TYPE_FOOTBALL_AMERICAN = 28
const val EXERCISE_TYPE_FOOTBALL_AUSTRALIAN = 29
const val EXERCISE_TYPE_FRISBEE_DISC = 31
const val EXERCISE_TYPE_GOLF = 32
const val EXERCISE_TYPE_GUIDED_BREATHING = 33
const val EXERCISE_TYPE_GYMNASTICS = 34
const val EXERCISE_TYPE_HANDBALL = 35
const val EXERCISE_TYPE_HIGH_INTENSITY_INTERVAL_TRAINING = 36
const val EXERCISE_TYPE_HIKING = 37
const val EXERCISE_TYPE_ICE_HOCKEY = 38
const val EXERCISE_TYPE_ICE_SKATING = 39
const val EXERCISE_TYPE_MARTIAL_ARTS = 44
const val EXERCISE_TYPE_OTHER_WORKOUT = 0
const val EXERCISE_TYPE_PADDLING = 46
const val EXERCISE_TYPE_PARAGLIDING = 47
const val EXERCISE_TYPE_PILATES = 48
const val EXERCISE_TYPE_RACQUETBALL = 50
const val EXERCISE_TYPE_ROCK_CLIMBING = 51
const val EXERCISE_TYPE_ROLLER_HOCKEY = 52
const val EXERCISE_TYPE_ROWING = 53
const val EXERCISE_TYPE_ROWING_MACHINE = 54
const val EXERCISE_TYPE_RUGBY = 55
const val EXERCISE_TYPE_RUNNING = 56
const val EXERCISE_TYPE_RUNNING_TREADMILL = 57
const val EXERCISE_TYPE_SAILING = 58
const val EXERCISE_TYPE_SCUBA_DIVING = 59
const val EXERCISE_TYPE_SKATING = 60
const val EXERCISE_TYPE_SKIING = 61
const val EXERCISE_TYPE_SNOWBOARDING = 62
const val EXERCISE_TYPE_SNOWSHOEING = 63
const val EXERCISE_TYPE_SOCCER = 64
const val EXERCISE_TYPE_SOFTBALL = 65
const val EXERCISE_TYPE_SQUASH = 66
const val EXERCISE_TYPE_STAIR_CLIMBING = 68
const val EXERCISE_TYPE_STAIR_CLIMBING_MACHINE = 69
const val EXERCISE_TYPE_STRENGTH_TRAINING = 70
const val EXERCISE_TYPE_STRETCHING = 71
const val EXERCISE_TYPE_SURFING = 72
const val EXERCISE_TYPE_SWIMMING_OPEN_WATER = 73
const val EXERCISE_TYPE_SWIMMING_POOL = 74
const val EXERCISE_TYPE_TABLE_TENNIS = 75
const val EXERCISE_TYPE_TENNIS = 76
const val EXERCISE_TYPE_VOLLEYBALL = 78
const val EXERCISE_TYPE_WALKING = 79
const val EXERCISE_TYPE_WATER_POLO = 80
const val EXERCISE_TYPE_WEIGHTLIFTING = 81
const val EXERCISE_TYPE_WHEELCHAIR = 82
const val EXERCISE_TYPE_YOGA = 83

enum class HealthConnectExercises(
    val type: String,
    @StringRes val title: Int,
    val icon: Int,
    val validExerciseTypes: Set<Int>,
) {
    OtherWorkout("Other Workout", R.string.other_workout, R.drawable.exercise, setOf(EXERCISE_TYPE_OTHER_WORKOUT)),
    Badminton("Badminton", R.string.badminton, R.drawable.badminton, setOf(EXERCISE_TYPE_BADMINTON)),
    Baseball("Baseball", R.string.baseball, R.drawable.baseball, setOf(EXERCISE_TYPE_BASEBALL)),
    Basketball("Basketball", R.string.basketball, R.drawable.basketball, setOf(EXERCISE_TYPE_BASKETBALL)),
    Biking("Biking", R.string.biking, R.drawable.bike, setOf(EXERCISE_TYPE_BIKING, EXERCISE_TYPE_BIKING_STATIONARY)),
    BootCamp("Boot Camp", R.string.boot_camp, R.drawable.camping, setOf(EXERCISE_TYPE_BOOT_CAMP)),
    Boxing("Boxing", R.string.boxing, R.drawable.boxing, setOf(EXERCISE_TYPE_BOXING)),
    Calisthenics("Calisthenics", R.string.calisthenics, R.drawable.yoga, setOf(EXERCISE_TYPE_CALISTHENICS)),
    Cricket("Cricket", R.string.cricket, R.drawable.cricket, setOf(EXERCISE_TYPE_CRICKET)),
    Dancing("Dancing", R.string.dancing, R.drawable.dance, setOf(EXERCISE_TYPE_DANCING)),
    Elliptical("Elliptical", R.string.elliptical, R.drawable.exercise, setOf(EXERCISE_TYPE_ELLIPTICAL)),
    ExerciseClass("Exercise Class", R.string.exercise_class, R.drawable.yoga, setOf(EXERCISE_TYPE_EXERCISE_CLASS)),
    Fencing("Fencing", R.string.fencing, R.drawable.fencing, setOf(EXERCISE_TYPE_FENCING)),
    FootballAmerican("Football American", R.string.football_american, R.drawable.american_football, setOf(EXERCISE_TYPE_FOOTBALL_AMERICAN)),
    FootballAustralian("Football Australian", R.string.football_australian, R.drawable.american_football, setOf(EXERCISE_TYPE_FOOTBALL_AUSTRALIAN)),
    FrisbeeDisc("Frisbee Disc", R.string.frisbee_disc, R.drawable.frisbee, setOf(EXERCISE_TYPE_FRISBEE_DISC)),
    Golf("Golf", R.string.golf, R.drawable.golf, setOf(EXERCISE_TYPE_GOLF)),
    GuidedBreathing("Guided Breathing", R.string.guided_breathing, R.drawable.breathe, setOf(EXERCISE_TYPE_GUIDED_BREATHING)),
    Gymnastics("Gymnastics", R.string.gymnastics, R.drawable.gymnastics, setOf(EXERCISE_TYPE_GYMNASTICS)),
    Handball("Handball", R.string.handball, R.drawable.handball, setOf(EXERCISE_TYPE_HANDBALL)),
    HighIntensityIntervalTraining("High Intensity Interval Training", R.string.weightlifting, R.drawable.weightlifting, setOf(EXERCISE_TYPE_HIGH_INTENSITY_INTERVAL_TRAINING)),
    Hiking("Hiking", R.string.hiking, R.drawable.hiking, setOf(EXERCISE_TYPE_HIKING)),
    IceHockey("Ice Hockey", R.string.ice_hockey, R.drawable.ice_hockey, setOf(EXERCISE_TYPE_ICE_HOCKEY)),
    IceSkating("Ice Skating", R.string.ice_skating, R.drawable.ice_skating, setOf(EXERCISE_TYPE_ICE_SKATING)),
    MartialArts("Martial Arts", R.string.martial_arts, R.drawable.boxing, setOf(EXERCISE_TYPE_MARTIAL_ARTS)),
    Paddling("Paddling", R.string.paddling, R.drawable.canoe, setOf(EXERCISE_TYPE_PADDLING)),
    Paragliding("Paragliding", R.string.paragliding, R.drawable.gliding, setOf(EXERCISE_TYPE_PARAGLIDING)),
    Pilates("Pilates", R.string.pilates, R.drawable.yoga, setOf(EXERCISE_TYPE_PILATES)),
    Racquetball("Racquetball", R.string.racquetball, R.drawable.racquetball, setOf(EXERCISE_TYPE_RACQUETBALL)),
    RockClimbing("Rock Climbing", R.string.rock_climbing, R.drawable.climbing, setOf(EXERCISE_TYPE_ROCK_CLIMBING)),
    RollerHockey("Roller Hockey", R.string.roller_hockey, R.drawable.roller_blades, setOf(EXERCISE_TYPE_ROLLER_HOCKEY)),
    Rowing("Rowing", R.string.rowing, R.drawable.canoe, setOf(EXERCISE_TYPE_ROWING, EXERCISE_TYPE_ROWING_MACHINE)),
    Rugby("Rugby", R.string.rugby, R.drawable.american_football, setOf(EXERCISE_TYPE_RUGBY)),
    Running("Running", R.string.running, R.drawable.run, setOf(EXERCISE_TYPE_RUNNING, EXERCISE_TYPE_RUNNING_TREADMILL)),
    Sailing("Sailing", R.string.sailing, R.drawable.sailing, setOf(EXERCISE_TYPE_SAILING)),
    ScubaDiving("Scuba Diving", R.string.scuba_diving, R.drawable.scuba, setOf(EXERCISE_TYPE_SCUBA_DIVING)),
    Skating("Skating", R.string.ice_skating, R.drawable.ice_skating, setOf(EXERCISE_TYPE_SKATING)),
    Skiing("Skiing", R.string.skiing, R.drawable.skiing, setOf(EXERCISE_TYPE_SKIING)),
    Snowboarding("Snowboarding", R.string.snowboarding, R.drawable.snowboard, setOf(EXERCISE_TYPE_SNOWBOARDING)),
    Snowshoeing("Snowshoeing", R.string.snowshoeing, R.drawable.snow_boot, setOf(EXERCISE_TYPE_SNOWSHOEING)),
    Soccer("Soccer", R.string.soccer, R.drawable.soccer, setOf(EXERCISE_TYPE_SOCCER)),
    Softball("Softball", R.string.softball, R.drawable.softball, setOf(EXERCISE_TYPE_SOFTBALL)),
    Squash("Squash", R.string.squash, R.drawable.squash, setOf(EXERCISE_TYPE_SQUASH)),
    StairClimbing("Stair Climbing", R.string.stair_climbing, R.drawable.stairs, setOf(EXERCISE_TYPE_STAIR_CLIMBING, EXERCISE_TYPE_STAIR_CLIMBING_MACHINE)),
    StrengthTraining("Strength Training", R.string.strength_training, R.drawable.weightlifting, setOf(EXERCISE_TYPE_STRENGTH_TRAINING)),
    Stretching("Stretching", R.string.stretching, R.drawable.yoga, setOf(EXERCISE_TYPE_STRETCHING)),
    Surfing("Surfing", R.string.surfing, R.drawable.surfing, setOf(EXERCISE_TYPE_SURFING)),
    Swimming("Swimming", R.string.swimming, R.drawable.swimming, setOf(EXERCISE_TYPE_SWIMMING_OPEN_WATER, EXERCISE_TYPE_SWIMMING_POOL)),
    TableTennis("Table Tennis", R.string.table_tennis, R.drawable.table_tennis, setOf(EXERCISE_TYPE_TABLE_TENNIS)),
    Tennis("Tennis", R.string.tennis, R.drawable.tennis, setOf(EXERCISE_TYPE_TENNIS)),
    Volleyball("Volleyball", R.string.volleyball, R.drawable.volleyball, setOf(EXERCISE_TYPE_VOLLEYBALL)),
    Walking("Walking", R.string.walking, R.drawable.walk, setOf(EXERCISE_TYPE_WALKING)),
    WaterPolo("Water Polo", R.string.water_polo, R.drawable.water_polo, setOf(EXERCISE_TYPE_WATER_POLO)),
    Weightlifting("Weightlifting", R.string.weightlifting, R.drawable.weightlifting, setOf(EXERCISE_TYPE_WEIGHTLIFTING)),
    Wheelchair("Wheelchair", R.string.wheelchair, R.drawable.wheelchair, setOf(EXERCISE_TYPE_WHEELCHAIR)),
    Yoga("Yoga", R.string.yoga, R.drawable.yoga, setOf(EXERCISE_TYPE_YOGA)),
    ;
    companion object {

        fun findTypeByExerciseType(exerciseType: Int): String? {
            return entries.firstOrNull { exerciseType in it.validExerciseTypes }?.type
        }

        fun findExerciseByType(exerciseType: Int): HealthConnectExercises? {
            return entries.firstOrNull { exerciseType in it.validExerciseTypes }
        }

        fun getIconByType(type: String): Int {
            return HealthConnectExercises.entries.find { it.type == type }?.icon ?: R.drawable.ecg_heart_24px
        }
    }
}
