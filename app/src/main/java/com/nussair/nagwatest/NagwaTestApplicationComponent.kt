package com.nussair.nagwatest

import com.nussair.nagwatest.view.activities.MainActivity
import dagger.Component

@Component
interface NagwaTestApplicationComponent {

    fun inject(mainActivity: MainActivity)
}