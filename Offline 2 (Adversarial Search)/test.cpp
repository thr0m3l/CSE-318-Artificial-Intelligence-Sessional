#include <bits/stdc++.h>

using namespace std;

#define NEG_INF -9999999
#define POS_INF 9999999
#define pr pair<int, int>

int W1, W2, W3, W4;
int additionalMove, stonesCaptured;
int player0Heuristic, player1Heuristic;
int depthMax = 7;

class Mancala {
public:
  int pit[2][6];
  int store[2];

  Mancala() {
    for (int i = 0; i < 2; i++) {
      store[i] = 0;
      for (int j = 0; j < 6; j++) {
        pit[i][j] = 4;
      }
    }
  }
  Mancala(int a) {
    for (int i = 0; i < 2; i++) {
      store[i] = 24;
      for (int j = 0; j < 6; j++) {
        pit[i][j] = 0;
      }
    }
  }

  int Move(int player, int idx) {
    if (!pit[player][idx])
      return player;

    int currentSide = player;
    int currentSeed = pit[currentSide][idx];
    int currentPit = idx + 1;
    int nextPlayer = (player + 1) % 2;
    pit[player][idx] = 0;

    while (currentSeed != 0) {
      if (currentPit == 6) // change side
      {
        currentPit = 0;

        if (currentSide == player) {
          store[player]++;
          currentSeed--;
          if (!currentSeed) {
            nextPlayer = player; // bonus move
          }
        }
        currentSide = (currentSide + 1) % 2;
        continue;
      }

      if (currentSeed == 1 && pit[currentSide][currentPit] == 0 &&
          currentSide == player) // capture
      {
        store[player] = store[player] + pit[nextPlayer][5 - currentPit] + 1;
        pit[nextPlayer][5 - currentPit] = 0;
        currentSeed--;
        stonesCaptured += pit[nextPlayer][5 - currentPit];
        continue;
      }

      if (currentPit < 6) {
        pit[currentSide][currentPit]++;
        currentSeed--;
        currentPit++;
        continue;
      }
    }

    return nextPlayer;
  }

  Mancala CopyBoard() {
    Mancala tempBoard;

    tempBoard.store[0] = this->store[0];
    tempBoard.store[1] = this->store[1];

    for (int i = 0; i < 6; i++) {
      tempBoard.pit[0][i] = this->pit[0][i];
      tempBoard.pit[1][i] = this->pit[1][i];
    }

    return tempBoard;
  }

  int GameEnd() // 0 for player 0, 1 for player 1, 2 for draw, 3 for unfinished
  {
    int total0 = 0;
    int total1 = 0;

    for (int i = 0; i < 6; i++) {
      total0 += pit[0][i];
      total1 += pit[1][i];
    }

    // giving remaining seeds to other player
    if (total0 == 0) {
      store[1] += total1;
      for (int i = 0; i < 6; i++) {
        pit[1][i] = 0;
      }
      // cout << "check total0\n";
      // total1 = 0;
    } else if (total1 == 0) {
      store[0] += total0;
      for (int i = 0; i < 6; i++) {
        pit[0][i] = 0;
      }
      // cout << "check total1\n";
      total0 = 0;
    }

    if (total0 == 0 && total1 == 0) {
      // cout << "\nFinal Board\n";
      // PrintBoard();
      if (store[0] == store[1])
        return 2;
      else if (store[0] > store[1])
        return 0;
      else
        return 1;
    }

    else
      return 3; // game not terminated
  }

  void PrintBoard() {
    cout << endl;
    for (int i = 0; i < 6; i++) {
      cout << "  " << pit[0][6 - i - 1];
    }

    cout << endl << store[0];
    cout << "                  ";
    cout << store[1] << endl;

    for (int i = 0; i < 6; i++) {
      cout << "  " << pit[1][i];
    }
    cout << endl;
  }
};

int H1(Mancala board, int player) {
  return board.store[player] - board.store[1 - player];
}

int H2(Mancala board, int player) {
  int tempPlayer = 0, tempOpp = 0;
  int temp = board.store[player] - board.store[1 - player];
  for (int i = 0; i < 6; i++) {
    tempPlayer += board.pit[player][i];
    tempOpp += board.pit[1 - player][i];
  }
  return W1 * temp + W2 * (tempPlayer - tempOpp);
}

int H3(Mancala board, int player) {
  int tempPlayer = 0, tempOpp = 0;
  int temp = board.store[player] - board.store[1 - player];
  for (int i = 0; i < 6; i++) {
    tempPlayer += board.pit[player][i];
    tempOpp += board.pit[1 - player][i];
  }
  return W1 * temp + W2 * (tempPlayer - tempOpp) + W3 * additionalMove;
}

int H4(Mancala board, int player) {
  int tempPlayer = 0, tempOpp = 0;
  int temp = board.store[player] - board.store[1 - player];
  for (int i = 0; i < 6; i++) {
    tempPlayer += board.pit[player][i];
    tempOpp += board.pit[1 - player][i];
  }
  return W1 * temp + W2 * (tempPlayer - tempOpp) + W3 * additionalMove +
         W4 * stonesCaptured;
}

int ChooseHeuristic(Mancala board, int player, int choice) {
  if (choice == 1)
    return H1(board, player);
  else if (choice == 2)
    return H2(board, player);
  else if (choice == 3)
    return H3(board, player);
  else if (choice == 4)
    return H4(board, player);
}

bool TerminalTest(Mancala board, int depth, int player) {
  if (depth == 1) {
    return true;
  }
  int sumPlayer = 0;
  int sumOpp = 0;

  for (int i = 0; i < 6; i++) {
    sumPlayer += board.pit[player][i];
    sumOpp += board.pit[1 - player][i];
  }

  if (sumPlayer == 0 || sumOpp == 0)
    return true;
  else
    return false;
}

pr MIN_VALUE(Mancala board, int alpha, int beta, int depth, int player);

pr MAX_VALUE(Mancala board, int alpha, int beta, int depth, int player) {
  if (TerminalTest(board, depth, player)) {
    if (player == 0)
      return make_pair(ChooseHeuristic(board, player, player0Heuristic), -1);
    else
      return make_pair(ChooseHeuristic(board, player, player0Heuristic), -1);
  }

  pr v = make_pair(NEG_INF, -1);

  for (int i = 0; i < 6; i++) {
    Mancala tempBoard;
    tempBoard = board.CopyBoard();

    if (tempBoard.pit[player][i] == 0)
      continue;

    int nxtTurn = tempBoard.Move(player, i);
    int tempV;

    if (nxtTurn == player) // bonus move
    {
      additionalMove++;
      tempV = MAX_VALUE(tempBoard, alpha, beta, depth - 1, player).first;
    } else {
      tempV = MIN_VALUE(tempBoard, alpha, beta, depth - 1, (1 - player)).first;
    }

    if (v.first < tempV) // maximizing
    {
      v.first = tempV;
      v.second = i;
    }

    if (v.first >= beta) // for pruning
    {
      return v;
    }

    alpha = max(alpha, v.first);
  }

  return v;
}

pr MIN_VALUE(Mancala board, int alpha, int beta, int depth, int player) {
  if (TerminalTest(board, depth, player)) {
    if (player == 0)
      return make_pair(ChooseHeuristic(board, player, player0Heuristic), -1);
    else
      return make_pair(ChooseHeuristic(board, player, player0Heuristic), -1);
  }

  pr v = make_pair(POS_INF, -1);

  for (int i = 0; i < 6; i++) {
    Mancala tempBoard;
    tempBoard = board.CopyBoard();
    if (tempBoard.pit[player][i] == 0)
      continue;

    int nxtTurn = tempBoard.Move(player, i);
    int tempV;

    if (nxtTurn == player) {
      additionalMove--;
      tempV = MIN_VALUE(tempBoard, alpha, beta, depth - 1, player).first;
    } else {
      tempV = MAX_VALUE(tempBoard, alpha, beta, depth - 1, (1 - player)).first;
    }

    if (v.first > tempV) // minimizing
    {
      v.first = tempV;
      v.second = i;
    }
    if (v.first <= alpha) // for pruning
    {
      return v;
    }

    beta = min(beta, v.first);
  }

  return v;
}

int MinMax(Mancala board, int player) // returns the index to be played
{
  int alpha = NEG_INF;
  int beta = POS_INF;

  additionalMove = 0;
  stonesCaptured = 0;

  pr v;
  v = MAX_VALUE(board, alpha, beta, depthMax, player);

  return v.second; // second is the index which will be played, -1 if reached a
                   // result/depth
}

int main() {
  srand(time(NULL));
  freopen("output.txt", "w", stdout);
  // Mancala drawn(1);
  Mancala board;
  cout << "Initial State : \n";
  board.PrintBoard();

  player0Heuristic = rand() % 4;
  player1Heuristic = rand() % 4;

  W1 = 20;
  W2 = 30;
  W3 = 40;
  W4 = 50;

  int player = rand() % 2;
  int idx;
  int checkflag;

  //    if(board.GameEnd() == 2)
  //    {
  //        cout << "\nMatch Drawn\n";
  //        return 0;
  //    }

  while (true) {
    cout << "\nPlayer" << player << "'s turn\n";
    idx = MinMax(board, player);
    cout << "Moving " << idx + 1 << "th Position\n";

    player = board.Move(player, idx);
    board.PrintBoard();
    checkflag = board.GameEnd();

    if (checkflag == 0) {
      cout << "\nPlayer 0 is the winner\n";
      break;
    } else if (checkflag == 1) {
      cout << "\nPlayer 1 is the winner\n";
      break;
    } else if (checkflag == 2) {
      cout << "\nMatch Drawn\n";
      break;
    }
  }
  return 0;
}