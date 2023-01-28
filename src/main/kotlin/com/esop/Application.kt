package com.esop

import com.esop.constant.*
import com.esop.service.UserService
import io.micronaut.runtime.Micronaut.run
import kotlin.math.floor
import kotlinx.coroutines.*


fun moveEsopsFromVestToNonPerf() {
	var allUsers = UserService.userList

	for(user in allUsers){

		for(inventory in user.value.uservestedInventories){

			var currentTime = System.currentTimeMillis()
			var vestedInventory = inventory.getVestInventory()
			var movedVestedInventory = inventory.getMovedVestedInventory()

			var firstMove:Long = floor((vestedInventory * VESTING_PERCENTAGE[0]/100).toDouble()).toLong()
			var secondMove:Long = floor((vestedInventory * VESTING_PERCENTAGE[1]/100).toDouble()).toLong()
			var thirdMove:Long = floor((vestedInventory * VESTING_PERCENTAGE[2]/100).toDouble()).toLong()

			if((currentTime - inventory.getTimeStamp()) >= VESTING_PERIOD[3] ){
				user.value.addToNonPerfInventory(vestedInventory - movedVestedInventory)
				inventory.setMovedVestedInventory(vestedInventory)
			}
			else {
				var moveInventory:Long = 0
				if((currentTime - inventory.getTimeStamp()) >= VESTING_PERIOD[0] && firstMove>movedVestedInventory){
					moveInventory += firstMove
				}
				if((currentTime - inventory.getTimeStamp()) >= VESTING_PERIOD[1] && (firstMove+secondMove)>movedVestedInventory){
						moveInventory += secondMove
				}
				if((currentTime - inventory.getTimeStamp()) >= VESTING_PERIOD[2] && (firstMove+secondMove+thirdMove)>movedVestedInventory){
					moveInventory += thirdMove
				}

				user.value.addToNonPerfInventory(moveInventory)
				inventory.setMovedVestedInventory(movedVestedInventory+moveInventory)
			}
		}
	}
}


fun main(args: Array<String>) {

		GlobalScope.launch { // launch a new coroutine and continue
			while (true){
				delay(1000)
				moveEsopsFromVestToNonPerf()
			}
		}

	run(*args)
}



