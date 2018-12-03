// ModulusChecker.cpp : This file contains the 'main' function. Program execution begins and ends there.
//

#include "pch.h"
#include <iostream>
#include <string>
#include <conio.h>
#include <stdio.h>

static int playerCount;

int GetInt();
void variant_playerID_plus_playercount_minus_one(int);
void variant_playerID_plus_one(int);

int main()
{
	playerCount = GetInt();
	std::cout << std::endl;
	std::cout << "Player count is: " << playerCount << std::endl;
	std::cout << std::endl;
	
	int agentID = 0;

	variant_playerID_plus_playercount_minus_one(agentID);
	std::cout << std::endl;
	variant_playerID_plus_one(agentID);

	_getch();

	return 0;
}

int GetInt()
{
	int temptInt;

	std::cout << "How many players are in the game? (Hint: Enter an integer) " << std::endl;

	std::cin >> temptInt;

	while (!std::cin.good())
	{
		std::cin.clear();
		std::cin.ignore();
		std::cout << "That is not a valid input, please enter an integer." << std::endl;
		std::cin >> temptInt;
	}

	return temptInt;
}

void variant_playerID_plus_playercount_minus_one(int agentID)
{
	std::cout << "Simulating variant (playerID + playercount -1) % playercount" << std::endl;

	int x = 0;
	while (x < playerCount)
	{
		int id = (agentID + playerCount - 1) % playerCount;
		std::cout << "AgentID is " << agentID << " Next agentID is: " << id << std::endl;
		agentID++;
		x++;
	}
}

void variant_playerID_plus_one(int agentID)
{
	std::cout << "Simulating variant (playerID + 1) % playercount" << std::endl;

	int x = 0;
	while (x < playerCount)
	{
		int id = (agentID + 1) % playerCount;
		std::cout << "AgentID is " << agentID << " Next agentID is: " << id << std::endl;
		agentID++;
		x++;
	}
}
