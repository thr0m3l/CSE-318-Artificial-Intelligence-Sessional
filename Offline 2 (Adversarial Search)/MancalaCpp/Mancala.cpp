#include <bits/stdc++.h>
#include <cstdlib>
#include <iostream>
#include <utility>
using namespace std;

#define NEG_INF -999999
#define POS_INF 999999
#define MAX_DEPTH 5

int W1 = 30, W2 = 40, W3 = 50, W4 = 60;
int freeMove = 0, captured = 0;
int p1h = 4, p2h = 3;

class Mancala {
public:
  int bin[2][6];
  int storage[2];

  Mancala() {
    for (int i = 0; i < 2; i++) {
      storage[i] = 0;
      for (int j = 0; j < 6; j++) {
        bin[i][j] = 4;
      }
    }
  }

  int move(int player, int index) {
    if (!bin[player][index])
      return player;

    int current = player;
    int stones = bin[current][index];
    int currentBin = index + 1;
    int nextPlayer = (current + 1) % 2;
    bin[player][index] = 0;

    while (stones != 0) {
      if (current == 6) {
        current = 0;

        if (current == player) {
          storage[player]++;
          stones--;
          if (!stones) {
            nextPlayer = player;
          }
        }
        current = (current + 1) % 2;
        continue;
      }

      if (stones == 1 && bin[current][currentBin] == 0 && current == player) {
        // capture
        storage[player] += bin[nextPlayer][5 - currentBin] + 1;
        captured += bin[nextPlayer][5 - currentBin];
        bin[nextPlayer][5 - currentBin] = 0;
        stones--;
        continue;
      }

      if (currentBin < 6) {
        bin[current][currentBin]++;
        stones--;
        currentBin++;
        continue;
      }
    }
    return nextPlayer;
  }

  Mancala copy() {
    Mancala ret;
    ret.storage[0] = this->storage[0];
    ret.storage[1] = this->storage[1];

    for (int i = 0; i < 6; i++) {
      ret.bin[0][i] = this->bin[0][i];
      ret.bin[1][i] = this->bin[1][i];
    }
    return ret;
  }

  int gameOver() {
    int sum0 = rowsum(0);
    int sum1 = rowsum(1);

    if (!sum0) {
      storage[1] += sum1;
      setzero(1);
    } else if (!sum1) {
      storage[0] += sum0;
      setzero(0);
      sum0 = 0;
    }

    if (!sum0 && !sum1) {
      if (storage[0] > storage[1]) {
        return 0;
      } else if (storage[0] == storage[1]) {
        return 2;
      } else {
        return 1;
      }
    }

    // default value, game not finished
    return 3;
  }

  void print() {
    cout << endl;
    for (int i = 0; i < 6; i++) {
      cout << " " << bin[0][6 - i - 1];
    }
    cout << endl << storage[0] << "\t    ";
    cout << storage[1] << endl;

    for (int i = 0; i < 6; i++) {
      cout << " " << bin[1][i];
    }
    cout << endl;
  }

  int rowsum(int r) {
    int sum = 0;
    for (int i = 0; i < 6; i++) {
      sum += bin[r][i];
    }
    return sum;
  }

  void setzero(int r) {
    for (int i = 0; i < 6; i++) {
      bin[r][i] = 0;
    }
  }
};

int h1(Mancala board, int player) {
  return board.storage[player] - board.storage[1 - player];
}

int h2(Mancala board, int player) {
  int tempPlayer = 0, tempOpp = 0;
  int temp = board.storage[player] - board.storage[1 - player];

  for (int i = 0; i < 6; i++) {
    tempPlayer += board.bin[player][i];
    tempOpp += board.bin[1 - player][i];
  }
  return W1 * temp + W2 * (tempPlayer - tempOpp);
}

int h3(Mancala board, int player) {
  int tempPlayer = 0, tempOpp = 0;
  int temp = board.storage[player] - board.storage[1 - player];

  for (int i = 0; i < 6; i++) {
    tempPlayer += board.bin[player][i];
    tempOpp += board.bin[1 - player][i];
  }
  return W1 * temp + W2 * (tempPlayer - tempOpp) + W3 * freeMove;
}

int h4(Mancala board, int player) {
  int tempPlayer = 0, tempOpp = 0;
  int temp = board.storage[player] - board.storage[1 - player];
  for (int i = 0; i < 6; i++) {
    tempPlayer += board.bin[player][i];
    tempOpp += board.bin[1 - player][i];
  }
  return W1 * temp + W2 * (tempPlayer - tempOpp) + W3 * freeMove +
         W4 * captured;
}

int heuristic(Mancala board, int player, int h) {

  if (h == 1) {
    return h1(board, player);
  } else if (h == 2) {
    return h2(board, player);
  } else if (h == 3) {
    return h3(board, player);
  } else if (h == 4) {
    return h4(board, player);
  }
}

bool isLeaf(Mancala board, int depth, int player) {
  if (depth == 1) {
    return true;
  }

  int sumPlayer = board.rowsum(player);
  int sumOpp = board.rowsum((player + 1) % 2);

  if (sumPlayer == 0 || sumOpp == 0) {
    return true;
  } else {
    return false;
  }
}

pair<int, int> minval(Mancala board, int alpha, int beta, int depth,
                      int player);

pair<int, int> maxval(Mancala board, int alpha, int beta, int depth,
                      int player) {

  cout << "maxval" << endl;
  cout << alpha << " " << beta << " " << depth << " " << player << endl;
  board.print();

  if (isLeaf(board, depth, player)) {
    if (player == 0)
      return {heuristic(board, player, p1h), -1};
    else
      return {heuristic(board, player, p1h), -1};
  }

  pair<int, int> v = {NEG_INF, -1};

  for (int i = 0; i < 6; i++) {
    Mancala tempBoard;
    tempBoard = board.copy();

    if (tempBoard.bin[player][i] == 0)
      continue;

    int nxtTurn = tempBoard.move(player, i);
    int tempV;

    if (nxtTurn == player) // bonus move
    {
      freeMove++;
      tempV = maxval(tempBoard, alpha, beta, depth - 1, player).first;
    } else {
      tempV = minval(tempBoard, alpha, beta, depth - 1, (1 - player)).first;
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

pair<int, int> minval(Mancala board, int alpha, int beta, int depth,
                      int player) {

  cout << "minval" << endl;
  cout << alpha << " " << beta << " " << depth << " " << player << endl;
  board.print();
  if (isLeaf(board, depth, player)) {
    if (player == 0)
      return make_pair(heuristic(board, player, p1h), -1);
    else
      return make_pair(heuristic(board, player, p1h), -1);
  }

  pair<int, int> v = make_pair(POS_INF, -1);

  for (int i = 0; i < 6; i++) {
    Mancala tempBoard;
    tempBoard = board.copy();
    if (tempBoard.bin[player][i] == 0)
      continue;

    int nxtTurn = tempBoard.move(player, i);
    int tempV;

    if (nxtTurn == player) {
      freeMove--;
      tempV = minval(tempBoard, alpha, beta, depth - 1, player).first;
    } else {
      tempV = maxval(tempBoard, alpha, beta, depth - 1, (1 - player)).first;
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

int minimax(Mancala board, int player) {
  freeMove = 0;
  captured = 0;

  pair<int, int> v;
  v = maxval(board, NEG_INF, POS_INF, MAX_DEPTH, player);

  return v.second;
}

int main() {

  Mancala board;
  board.print();

  int player = 0, move;

  while (true) {
    cout << "\nPlayer" << player << "'s turn\n";
    move = minimax(board, player);
    cout << "Moving " << move + 1 << "th position";
    player = board.move(player, move);
    board.print();

    int status = board.gameOver();

    if (status <= 1) {
      cout << "\nPlayer " + to_string(status) + " is the winner\n";
      break;
    } else if (status == 2) {
      cout << "\nMatch drawn\n";
      break;
    }
  }

  return 0;
}

int main2() {
  Mancala board;

  board.storage[0] = 6;
  board.storage[1] = 9;

  return 0;
}
