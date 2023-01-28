package com.esop

import com.esop.constant.FIRST_VESTING_PERIOD
import com.esop.constant.FOURTH_VESTING_PERIOD
import com.esop.constant.SECOND_VESTING_PERIOD
import com.esop.constant.THIRD_VESTING_PERIOD
import com.esop.service.UserService
import io.micronaut.runtime.Micronaut.run
import kotlin.math.floor
import kotlinx.coroutines.*


fun moveEsopsFromVestToNonPerf() {
	var allUsers = UserService.userList

	for(user in allUsers){
		var currentTime = System.currentTimeMillis()
		var vestedInventory = user.value.vestedInventory.getVestInventory()
		var movedVestedInventory = user.value.vestedInventory.getMovedVestedInventory()

		var firstMove:Long = floor(vestedInventory*0.3).toLong()
		var secondMove:Long = floor(vestedInventory*0.2).toLong()
		var thirdMove:Long = floor(vestedInventory*0.1).toLong()

		if((currentTime-user.value.vestedInventory.getTimeStamp()) >= FOURTH_VESTING_PERIOD ){
			user.value.addToNonPerfInventory(vestedInventory - movedVestedInventory)
			user.value.vestedInventory.setMovedVestedInventory(vestedInventory)
		}
		else if((currentTime-user.value.vestedInventory.getTimeStamp()) >= THIRD_VESTING_PERIOD){
			var moveInventory:Long = 0
			if(movedVestedInventory < firstMove)
				moveInventory += firstMove
			if(movedVestedInventory < (firstMove+secondMove))
				moveInventory += secondMove
			if(movedVestedInventory < (firstMove+secondMove+thirdMove))
				moveInventory += thirdMove
			user.value.addToNonPerfInventory(moveInventory)
			user.value.vestedInventory.setMovedVestedInventory(movedVestedInventory+moveInventory)
		}
		else if((currentTime-user.value.vestedInventory.getTimeStamp()) >= SECOND_VESTING_PERIOD){
			var moveInventory:Long = 0
			if(movedVestedInventory < firstMove)
				moveInventory += firstMove
			if(movedVestedInventory < (firstMove+secondMove))
				moveInventory += secondMove
			user.value.addToNonPerfInventory(moveInventory)
			user.value.vestedInventory.setMovedVestedInventory(movedVestedInventory+moveInventory)
		}
		else if((currentTime-user.value.vestedInventory.getTimeStamp()) >= FIRST_VESTING_PERIOD){
			var moveInventory:Long = 0
			if(movedVestedInventory < firstMove)
				moveInventory += firstMove

			user.value.addToNonPerfInventory(moveInventory)
			user.value.vestedInventory.setMovedVestedInventory(movedVestedInventory+moveInventory)
		}
	}

}


fun main(args: Array<String>) {

		GlobalScope.launch { // launch a new coroutine and continue
			while (true){
				moveEsopsFromVestToNonPerf()

			}

		}


	run(*args)
}



