/*
 * Copyright (c) {year} Sergei Kornilov
 * Licensed under the Apache License, Version 2.0
 */

package love.korni.manipulator.core.caldron;

import java.lang.reflect.Type;

/**
 * Caldron
 *
 * @author Sergei_Konilov
 */
public interface Caldron {

    /**
     * Получить шестерню по типу класса из DI контейнера, если подобная была создана ранее.
     * В противном случае будет произведён процесс создания шестерни.
     *
     * @param type тип класса
     * @param <T>  тип объекта
     * @return объект типа T
     */
    <T> T getGearOfType(Class<T> type);

    /**
     * Получить шестерню по типу класса из DI контейнера, если подобная была создана ранее.
     * В противном случае будет произведён процесс создания шестерни.
     *
     * @param type тип класса
     * @param <T>  тип объекта
     * @return объект типа T
     */
    <T> T getGearOfType(Type type);

    /**
     * Получить шестерню по типу класса из DI контейнера, если подобная была создана ранее.
     * В противном случае будет произведён процесс создания шестерни, используя конструктор с переданными аргументами.
     * При создании будет произведен поиск подходящего под переданные аргументы конструктора,
     * игнорируя существующий {@link love.korni.manipulator.core.annotation.Autoinject} конструктор при его наличии.
     *
     * @param type тип класса
     * @param args аргументы конструктора
     * @param <T>  тип объекта
     * @return объект типа T
     */
    <T> T getGearOfType(Class<T> type, Object... args);

    /**
     * Получить шестерню по имени и типу из DI контейнера, если была создана ранее.
     * В противном случае будет произведён процесс создания шестерни.
     * @param gearName имя шестерни
     * @param type тип класса
     * @return объект типа Т
     */
    <T> T getGearByName(String gearName, Class<T> type);

    GearFactory getGearFactory();

    /**
     * Запустить процесс очистки котла. Будет выполнен вызов публичных методов помеченных аннотацией {@link love.korni.manipulator.core.annotation.PreDestroy}.
     * После чего все шестерни будут удалены, и котёл придёт в негодность.
     */
    void close();
}
