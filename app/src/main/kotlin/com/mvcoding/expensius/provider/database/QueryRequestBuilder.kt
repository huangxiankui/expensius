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

package com.mvcoding.expensius.provider.database

import com.mvcoding.expensius.provider.database.table.Column
import com.mvcoding.expensius.provider.database.table.Table

fun select(columns: Array<Column>) = Select(columns)
fun select(vararg table: Table) = Select(*table)

interface Element {
    fun elementPartSql(): String
}

abstract class QueryRequest(
        protected val previousElement: Element,
        val columns: Array<out Column>,
        val tables: Array<Table>,
        val arguments: Array<String>) : Element {
    fun sql(): String = "${if (previousElement is QueryRequest) previousElement.sql() else previousElement.elementPartSql() } ${elementPartSql()}"
}

class Select(private val columns: Array<out Column>) : Element {
    constructor(vararg table: Table) : this(table.map { it.columns().map { it } }.flatten().toTypedArray())

    fun from(table: Table) = From(this, columns, table, emptyArray())
    override fun elementPartSql() = "SELECT ${columns.joinToString { it.toString() }}"
}

class From(
        previousElement: Element,
        columns: Array<out Column>,
        table: Table,
        arguments: Array<String>) : QueryRequest(previousElement, columns, arrayOf(table), arguments) {

    override fun elementPartSql() = "FROM ${tables.last().name}"
    fun leftJoin(table: Table, on: String) = Join(this, columns, tables.plus(table), arguments, "LEFT", on)
    fun where(clause: String, vararg arguments: String = emptyArray()) = Where(this,
                                                                               columns,
                                                                               tables,
                                                                               arguments.toList().toTypedArray(),
                                                                               "WHERE",
                                                                               clause)
}

class Join(
        previousElement: Element,
        columns: Array<out Column>,
        tables: Array<Table>,
        arguments: Array<String>,
        private val joinType: String,
        private val on: String) : QueryRequest(previousElement, columns, tables, arguments) {

    override fun elementPartSql() = "$joinType JOIN ${tables.last().name} ON $on"
    fun leftJoin(table: Table, on: String) = Join(this, columns, tables.plus(table), arguments, "LEFT", on)
    fun where(clause: String, vararg arguments: String = emptyArray()) = Where(this,
                                                                               columns,
                                                                               tables,
                                                                               arguments.toList().toTypedArray(),
                                                                               "WHERE",
                                                                               clause)
}

class Where(
        previousElement: Element,
        columns: Array<out Column>,
        tables: Array<Table>,
        arguments: Array<String>,
        private val keyword: String,
        private val clause: String) : QueryRequest(previousElement, columns, tables, arguments) {

    override fun elementPartSql() = "$keyword $clause"

    fun and(clause: String, vararg arguments: String = emptyArray()) = Where(this,
                                                                             columns,
                                                                             tables,
                                                                             arguments.toList().toTypedArray(),
                                                                             "AND",
                                                                             clause)

    fun or(clause: String, vararg arguments: String = emptyArray()) = Where(this,
                                                                            columns,
                                                                            tables,
                                                                            arguments.toList().toTypedArray(),
                                                                            "OR",
                                                                            clause)

    fun groupBy(vararg groupByColumns: Column) = GroupBy(this, columns, tables, arguments, groupByColumns.toList().toTypedArray())
    fun orderBy(vararg orderByColumns: Order) = OrderBy(this, columns, tables, arguments, orderByColumns.toList().toTypedArray())
}

class GroupBy(
        previousElement: Element,
        columns: Array<out Column>,
        tables: Array<Table>,
        arguments: Array<String>,
        private val groupByColumns: Array<out Column>) : QueryRequest(previousElement, columns, tables, arguments) {

    override fun elementPartSql() = "GROUP BY ${groupByColumns.joinToString { it.name }}"

    fun orderBy(vararg orderByColumns: Order) = OrderBy(this, columns, tables, arguments, orderByColumns.toList().toTypedArray())
}

class OrderBy(
        previousElement: Element,
        columns: Array<out Column>,
        tables: Array<Table>,
        arguments: Array<String>,
        private val orders: Array<Order>) : QueryRequest(previousElement, columns, tables, arguments) {
    override fun elementPartSql() = "ORDER BY ${orders.joinToString { it.toString() }}"
}

data class Order(private val column: Column, private val orderDirection: OrderDirection) {
    override fun toString() = "${column.name} $orderDirection"
}

enum class OrderDirection {
    ASC, DESC
}