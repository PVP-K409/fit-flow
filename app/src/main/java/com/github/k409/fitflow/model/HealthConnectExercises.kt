package com.github.k409.fitflow.model

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
    val icon: Int,
    val validExerciseTypes: Set<Int>,
) {
    OtherWorkout("Other Workout", R.drawable.exercise, setOf(EXERCISE_TYPE_OTHER_WORKOUT)),
    Badminton("Badminton", R.drawable.badminton, setOf(EXERCISE_TYPE_BADMINTON)),
    Baseball("Baseball", R.drawable.baseball, setOf(EXERCISE_TYPE_BASEBALL)),
    Basketball("Basketball", R.drawable.basketball, setOf(EXERCISE_TYPE_BASKETBALL)),
    Biking("Biking", R.drawable.bike, setOf(EXERCISE_TYPE_BIKING, EXERCISE_TYPE_BIKING_STATIONARY)),
    BootCamp("Boot Camp", R.drawable.camping, setOf(EXERCISE_TYPE_BOOT_CAMP)),
    Boxing("Boxing", R.drawable.boxing, setOf(EXERCISE_TYPE_BOXING)),
    Calisthenics("Calisthenics", R.drawable.yoga, setOf(EXERCISE_TYPE_CALISTHENICS)),
    Cricket("Cricket", R.drawable.cricket, setOf(EXERCISE_TYPE_CRICKET)),
    Dancing("Dancing", R.drawable.dance, setOf(EXERCISE_TYPE_DANCING)),
    Elliptical("Elliptical", R.drawable.exercise, setOf(EXERCISE_TYPE_ELLIPTICAL)),
    ExerciseClass("Exercise Class", R.drawable.yoga, setOf(EXERCISE_TYPE_EXERCISE_CLASS)),
    Fencing("Fencing", R.drawable.fencing, setOf(EXERCISE_TYPE_FENCING)),
    FootballAmerican("Football American", R.drawable.american_football, setOf(EXERCISE_TYPE_FOOTBALL_AMERICAN)),
    FootballAustralian("Football Australian", R.drawable.american_football, setOf(EXERCISE_TYPE_FOOTBALL_AUSTRALIAN)),
    FrisbeeDisc("Frisbee Disc", R.drawable.frisbee, setOf(EXERCISE_TYPE_FRISBEE_DISC)),
    Golf("Golf", R.drawable.golf, setOf(EXERCISE_TYPE_GOLF)),
    GuidedBreathing("Guided Breathing", R.drawable.breathe, setOf(EXERCISE_TYPE_GUIDED_BREATHING)),
    Gymnastics("Gymnastics", R.drawable.gymnastics, setOf(EXERCISE_TYPE_GYMNASTICS)),
    Handball("Handball", R.drawable.handball, setOf(EXERCISE_TYPE_HANDBALL)),
    HighIntensityIntervalTraining("High Intensity Interval Training", R.drawable.weightlifting, setOf(EXERCISE_TYPE_HIGH_INTENSITY_INTERVAL_TRAINING)),
    Hiking("Hiking", R.drawable.hiking, setOf(EXERCISE_TYPE_HIKING)),
    IceHockey("Ice Hockey", R.drawable.ice_hockey, setOf(EXERCISE_TYPE_ICE_HOCKEY)),
    IceSkating("Ice Skating", R.drawable.ice_skating, setOf(EXERCISE_TYPE_ICE_SKATING)),
    MartialArts("Martial Arts", R.drawable.boxing, setOf(EXERCISE_TYPE_MARTIAL_ARTS)),
    Paddling("Paddling", R.drawable.canoe, setOf(EXERCISE_TYPE_PADDLING)),
    Paragliding("Paragliding", R.drawable.gliding, setOf(EXERCISE_TYPE_PARAGLIDING)),
    Pilates("Pilates", R.drawable.yoga, setOf(EXERCISE_TYPE_PILATES)),
    Racquetball("Racquetball", R.drawable.racquetball, setOf(EXERCISE_TYPE_RACQUETBALL)),
    RockClimbing("Rock Climbing", R.drawable.climbing, setOf(EXERCISE_TYPE_ROCK_CLIMBING)),
    RollerHockey("Roller Hockey", R.drawable.roller_blades, setOf(EXERCISE_TYPE_ROLLER_HOCKEY)),
    Rowing("Rowing", R.drawable.canoe, setOf(EXERCISE_TYPE_ROWING,EXERCISE_TYPE_ROWING_MACHINE)),
    Rugby("Rugby", R.drawable.american_football, setOf(EXERCISE_TYPE_RUGBY)),
    Running("Running", R.drawable.run, setOf(EXERCISE_TYPE_RUNNING, EXERCISE_TYPE_RUNNING_TREADMILL)),
    Sailing("Sailing", R.drawable.sailing, setOf(EXERCISE_TYPE_SAILING)),
    ScubaDiving("Scuba Diving", R.drawable.scuba, setOf(EXERCISE_TYPE_SCUBA_DIVING)),
    Skating("Skating", R.drawable.ice_skating, setOf(EXERCISE_TYPE_SKATING)),
    Skiing("Skiing", R.drawable.skiing, setOf(EXERCISE_TYPE_SKIING)),
    Snowboarding("Snowboarding", R.drawable.snowboard, setOf(EXERCISE_TYPE_SNOWBOARDING)),
    Snowshoeing("Snowshoeing", R.drawable.snow_boot, setOf(EXERCISE_TYPE_SNOWSHOEING)),
    Soccer("Soccer", R.drawable.soccer, setOf(EXERCISE_TYPE_SOCCER)),
    Softball("Softball", R.drawable.softball, setOf(EXERCISE_TYPE_SOFTBALL)),
    Squash("Squash", R.drawable.squash, setOf(EXERCISE_TYPE_SQUASH)),
    StairClimbing("Stair Climbing", R.drawable.stairs, setOf(EXERCISE_TYPE_STAIR_CLIMBING, EXERCISE_TYPE_STAIR_CLIMBING_MACHINE)),
    StrengthTraining("Strength Training", R.drawable.weightlifting, setOf(EXERCISE_TYPE_STRENGTH_TRAINING)),
    Stretching("Stretching", R.drawable.yoga, setOf(EXERCISE_TYPE_STRETCHING)),
    Surfing("Surfing", R.drawable.surfing, setOf(EXERCISE_TYPE_SURFING)),
    Swimming("Swimming", R.drawable.swimming, setOf(EXERCISE_TYPE_SWIMMING_OPEN_WATER, EXERCISE_TYPE_SWIMMING_POOL)),
    TableTennis("Table Tennis", R.drawable.table_tennis, setOf(EXERCISE_TYPE_TABLE_TENNIS)),
    Tennis("Tennis", R.drawable.tennis, setOf(EXERCISE_TYPE_TENNIS)),
    Volleyball("Volleyball", R.drawable.volleyball, setOf(EXERCISE_TYPE_VOLLEYBALL)),
    Walking("Walking", R.drawable.walk, setOf(EXERCISE_TYPE_WALKING)),
    WaterPolo("Water Polo", R.drawable.water_polo, setOf(EXERCISE_TYPE_WATER_POLO)),
    Weightlifting("Weightlifting", R.drawable.weightlifting, setOf(EXERCISE_TYPE_WEIGHTLIFTING)),
    Wheelchair("Wheelchair", R.drawable.wheelchair, setOf(EXERCISE_TYPE_WHEELCHAIR)),
    Yoga("Yoga", R.drawable.yoga, setOf(EXERCISE_TYPE_YOGA));
    companion object {

        fun findTypeByExerciseType(exerciseType: Int): String? {
            return entries.firstOrNull { exerciseType in it.validExerciseTypes }?.type
        }
        fun getIconByType(type: String): Int {
            return HealthConnectExercises.entries.find { it.type == type }?.icon ?: R.drawable.ecg_heart_24px
        }
    }
}




