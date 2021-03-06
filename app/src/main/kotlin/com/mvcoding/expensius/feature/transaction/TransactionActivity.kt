/*
 * Copyright (C) 2016 Mantas Varnagiris.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.mvcoding.expensius.feature.transaction

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import com.mvcoding.expensius.R
import com.mvcoding.expensius.feature.ActivityStarter
import com.mvcoding.expensius.feature.BaseActivity
import com.mvcoding.expensius.model.Transaction

class TransactionActivity : BaseActivity() {
    companion object {
        private const val EXTRA_TRANSACTION = "EXTRA_TRANSACTION"

        fun start(context: Context, transaction: Transaction) {
            ActivityStarter(context, TransactionActivity::class)
                    .extra(EXTRA_TRANSACTION, transaction)
                    .start()
        }
    }

    private val transactionView by lazy { findViewById(R.id.transactionView) as TransactionView }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_transaction)

        val transaction = intent.getSerializableExtra(EXTRA_TRANSACTION) as Transaction
        transactionView.init(transaction)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        transactionView.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.tag, menu)
        val menuItem = menu.findItem(R.id.action_archive)
        menuItem.isVisible = transactionView.isArchiveToggleVisible
        menuItem.title = transactionView.archiveToggleTitle
        return true
    }
}