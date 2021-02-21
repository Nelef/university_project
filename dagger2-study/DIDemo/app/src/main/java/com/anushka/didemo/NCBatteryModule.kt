package com.anushka.didemo

import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class NCBatteryModule { //@Binds 방식은 @Inject constructor() 를 해줘야함(    https://yuar.tistory.com/84)

    @Binds
    abstract fun bindsNCBattery(nickelCadmiumBattery: NickelCadmiumBattery):Battery
}