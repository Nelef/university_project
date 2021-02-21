package com.anushka.didemo

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [MemoryCardModule::class,NCBatteryModule::class]) //둘 차이 : https://yuar.tistory.com/84
interface SmartPhoneComponent {

   fun inject(mainActivity: MainActivity)
}

