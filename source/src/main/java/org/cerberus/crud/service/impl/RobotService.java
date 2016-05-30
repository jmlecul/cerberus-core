/*
 * Cerberus  Copyright (C) 2013  vertigo17
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This file is part of Cerberus.
 *
 * Cerberus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cerberus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cerberus.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.cerberus.crud.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.cerberus.crud.dao.IRobotDAO;
import org.cerberus.crud.entity.MessageGeneral;
import org.cerberus.crud.entity.Robot;
import org.cerberus.crud.service.IRobotCapabilityService;
import org.cerberus.crud.service.IRobotService;
import org.cerberus.enums.MessageEventEnum;
import org.cerberus.enums.MessageGeneralEnum;
import org.cerberus.exception.CerberusException;
import org.cerberus.util.answer.Answer;
import org.cerberus.util.answer.AnswerItem;
import org.cerberus.util.answer.AnswerList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author bcivel
 */
@Service
public class RobotService implements IRobotService {

	/** The associated {@link Logger} to this class */
	private static final Logger LOGGER = Logger.getLogger(RobotService.class);

	@Autowired
	private IRobotDAO robotDao;

	@Autowired
	private IRobotCapabilityService robotCapabilityService;

	@Override
	public AnswerItem<Robot> readByKeyTech(Integer robotid) {
		return fillCapabilities(robotDao.readByKeyTech(robotid));
	}

	@Override
	public AnswerItem<Robot> readByKey(String robot) {
		return fillCapabilities(robotDao.readByKey(robot));
	}

	@Override
	public AnswerList<Robot> readAll() {
		return readByCriteria(0, 0, "robot", "asc", null, null);
	}

	@Override
	public AnswerList<Robot> readByCriteria(int startPosition, int length, String columnName, String sort,
			String searchParameter, String string) {
		return fillCapabilities(robotDao.readByCriteria(startPosition, length, columnName, sort, searchParameter, string));
	}

	@Override
	public Answer create(Robot robot) {
		return robotDao.create(robot);
	}

	@Override
	public Answer delete(Robot robot) {
		return robotDao.delete(robot);
	}

	@Override
	public Answer update(Robot robot) {
		return robotDao.update(robot);
	}

	@Override
	public Robot convert(AnswerItem<Robot> answerItem) throws CerberusException {
		if (answerItem.isCodeEquals(MessageEventEnum.DATA_OPERATION_OK.getCode())) {
			// if the service returns an OK message then we can get the item
			return answerItem.getItem();
		}
		throw new CerberusException(new MessageGeneral(MessageGeneralEnum.DATA_OPERATION_ERROR));
	}

	@Override
	public List<Robot> convert(AnswerList<Robot> answerList) throws CerberusException {
		if (answerList.isCodeEquals(MessageEventEnum.DATA_OPERATION_OK.getCode())) {
			// if the service returns an OK message then we can get the item
			return answerList.getDataList();
		}
		throw new CerberusException(new MessageGeneral(MessageGeneralEnum.DATA_OPERATION_ERROR));
	}

	@Override
	public void convert(Answer answer) throws CerberusException {
		if (answer.isCodeEquals(MessageEventEnum.DATA_OPERATION_OK.getCode())) {
			// if the service returns an OK message then we can get the item
			return;
		}
		throw new CerberusException(new MessageGeneral(MessageGeneralEnum.DATA_OPERATION_ERROR));
	}

	private AnswerItem<Robot> fillCapabilities(AnswerItem<Robot> robotItem) {
		try {
			Robot robot = convert(robotItem);
			robot.setCapabilities(robotCapabilityService.convert(robotCapabilityService.readByRobot(robot.getRobot())));
		} catch (CerberusException e) {
			LOGGER.warn("Unable to flll robot capabilities due to " + e.getMessage());
		}
		return robotItem;
	}

	private AnswerList<Robot> fillCapabilities(AnswerList<Robot> robotList) {
		try {
			List<Robot> robots = convert(robotList);
			if (robots != null) {
				for (Robot robot : robots) {
					robot.setCapabilities(
							robotCapabilityService.convert(robotCapabilityService.readByRobot(robot.getRobot())));
				}
			}
		} catch (CerberusException e) {
			LOGGER.warn("Unable to fill robot capabilities due to " + e.getMessage());
		}
		return robotList;
	}

}
