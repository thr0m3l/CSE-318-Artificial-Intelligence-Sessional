#include <bits/stdc++.h>
#include <cstdlib>
#include <ctime>
#include <iterator>
#include <vector>
using namespace std;

#define INF 99999999

int W1, W2, W3, W4;

class Board {

public:
  /* 0-5 Player 1 bins left to right
   6 Player 1 Mancala
   7-12 Player 2 bins left to right
   13 player 2 Mancala
  */
  int bin[14];

  int turn;
  int freeMove;
  int captured;
  int heuristic_val;
  int heuristic;

  Board() {
    turn = 1;
    freeMove = 0;
    captured = 0;
    heuristic_val = 0;
    bin[6] = bin[13] = 0;

    for (int i = 0; i < 6; i++) {
      bin[i] = bin[i + 7] = 4;
    }
  }

  void print() {
    int count = 0;
    for (int i = 0; i < 14; i++)
      count += bin[i];

    // player 1 upper
    // player 2 lower
    cout << "----- Player 1-----" << endl;
    cout << "\t";
    for (int i = 5; i >= 0; i--)
      cout << bin[i] << " ";
    cout << "\t\n";

    cout << "(" << bin[6] << ")\t\t\t";
    cout << "(" << bin[13] << ")" << endl;

    cout << "\t";
    for (int i = 7; i <= 12; i++)
      cout << bin[i] << " ";
    cout << "\t\n";
    cout << "----- Player 2-----" << endl;

    cout << "Next move: Player " << turn << endl;
  }

  bool isLeaf() {
    int stones = 0;
    for (int i = 0; i <= 5; i++) {
      stones += bin[i];
    }
    if (stones == 0)
      return true;

    stones = 0;
    for (int i = 7; i <= 12; i++) {
      stones += bin[i];
    }
    if (stones == 0)
      return true;
    return false;
  }

  int heuristic1() { return bin[6] - bin[13]; }

  int heuristic2() {
    W1 = 16, W2 = 1, W3 = 0, W4 = 0;
    int stones_on_player1_side = 0, stones_on_player2_side = 0;
    for (int i = 0; i < 6; i++)
      stones_on_player1_side += bin[i];
    for (int i = 0; i < 6; i++)
      stones_on_player2_side += bin[i + 7];
    return heuristic1() +
           W2 * (stones_on_player1_side - stones_on_player2_side);
  }

  int heuristic3() {
    W1 = 16, W2 = 1, W3 = 4, W4 = 0;
    freeMove = 0;
    if (turn == 1) {
      for (int i = 0; i < 6; i++)
        if (bin[i] == (6 - i))
          freeMove++;
    } else {
      for (int i = 7; i < 14; i++)
        if (bin[i] == (14 - i))
          freeMove++;
      freeMove = (-freeMove);
    }
    return heuristic2() + W3 * freeMove;
  }

  int heuristic4() {
    W1 = 16, W2 = 1, W3 = 10, W4 = 20;
    return heuristic3() + W4 * captured;
  }

  int heuristic5() { return 2 * heuristic3() + 3 * heuristic4(); }

  int heuristic6(int turn) {
    int myStorage, opStorage;
    if (turn == 1) {
      myStorage = bin[6];
      opStorage = bin[13];
    } else {
      myStorage = bin[13];
      opStorage = bin[6];
    }
    return 2 * heuristic5() + 10 * myStorage - 5 * opStorage;
  }

  int evaluate(int heuristic_num, int turn) {
    int storage1 = bin[6], storage2 = bin[13];

    if (heuristic_num == 1) {
      return heuristic1();
    } else if (heuristic_num == 2) {
      return heuristic2();
    } else if (heuristic_num == 3) {
      return heuristic3();
    } else if (heuristic_num == 4) {
      return heuristic4();
    } else if (heuristic_num == 5) {
      return heuristic5();
    } else {
      return heuristic6(turn);
    }
  }
};

struct less_than_key {
  inline bool operator()(const Board &struct1, const Board &struct2) {
    return (struct1.heuristic_val < struct2.heuristic_val);
  }
};

vector<Board> getChildren(Board parent, int heuristic_choice);

int minimax(Board b, int depth, int alpha, int beta, int heuristic_choice);

// gameplay
Board human_agent(Board curr) {
  int side = curr.turn;

  cout << "Choose bin (1~6): ";
  int choice;
  cin >> choice;
  int pos = (side - 1) * 7 + choice;
  int stones = curr.bin[pos - 1];
  curr.bin[pos - 1] = 0;
  while (stones) {
    if ((side == 1 && pos == 13) || (side == 2 && pos == 6)) {
    } else {
      curr.bin[pos]++;
      stones--;
      if (stones == 0) {
        if (pos != ((side - 1) * 7 + 6)) {
          curr.turn = 3 - side;
        }
        int low = (side - 1) * 7, high = low + 5;
        if (curr.bin[pos] == 1 && pos >= low && pos <= high) {
          curr.bin[(side - 1) * 7 + 6] += (curr.bin[pos] + curr.bin[12 - pos]);
          curr.bin[pos] = curr.bin[12 - pos] = 0;
        }
      }
    }
    pos = (pos + 1) % 14;
  }
  return curr;
}

vector<Board> generateChildren(Board parent, int heuristic_choice) {
  vector<Board> children;
  int start, mancala_this, mancala_that;
  if (parent.turn == 1) { // player 1
    start = 0;
    mancala_this = 6;
    mancala_that = 13;
  } else { // player 2
    start = 7;
    mancala_this = 13;
    mancala_that = 6;
  }

  for (int i = 0; i < 6; i++) {
    int stones = parent.bin[start + i];

    if (stones <= 0) {
      continue;
    } else {
      Board child = parent; // valid choice
      child.bin[start + i] = 0;
      child.captured = 0;
      child.freeMove = 0;

      for (int j = 1;; j++) {
        int slot_id = (start + i + j) % 14;
        if (slot_id != mancala_that) {
          stones--;
          child.bin[slot_id]++;
        }

        if (stones == 0) {

          if (slot_id == mancala_this) {
            child.turn = parent.turn;
            child.freeMove = 1;
          } else
            child.turn = 3 - parent.turn;

          if (slot_id >= start && slot_id < mancala_this &&
              child.bin[slot_id] == 1) {
            int opposite_slot_id = 12 - slot_id;
            child.captured +=
                (child.bin[slot_id] + child.bin[opposite_slot_id]);
            child.bin[mancala_this] +=
                (child.bin[slot_id] + child.bin[opposite_slot_id]);
            child.bin[slot_id] = 0;
            child.bin[opposite_slot_id] = 0;
          }
          break;
        }
      }

      if (parent.turn == 2) {
        child.captured = -child.captured;

        child.freeMove = -child.freeMove;
      }
      if (child.isLeaf()) {

        int stones = 0;
        for (int i = 0; i <= 5; i++) {
          stones += child.bin[i];
          child.bin[i] = 0;
        }
        child.bin[6] += stones;

        child.captured += stones;

        stones = 0;
        for (int i = 7; i <= 12; i++) {
          stones += child.bin[i];
          child.bin[i] = 0;
        }
        child.bin[13] += stones;

        child.captured -= stones;
      }

      child.heuristic_val = child.evaluate(heuristic_choice, child.turn);
      children.push_back(child);
    }
  }

  sort(children.begin(), children.end(), less_than_key());
  if (parent.turn == 1) {

    reverse(children.begin(), children.end());
  }

  return children;
}

int minimax(Board b, int depth, int alpha, int beta, int heuristic_choice) {

  if (b.isLeaf() || depth == 0) {
    return b.evaluate(heuristic_choice, b.turn);
  }
  vector<Board> ch = generateChildren(b, heuristic_choice);
  int ch_cnt = ch.size();
  // if b is a max node
  if (b.turn == 1) {
    int v = alpha;
    for (int i = 0; i < ch_cnt; i++) {
      int v1 = minimax(ch[i], depth - 1, v, beta, heuristic_choice);
      if (v1 > v)
        v = v1;
      if (v > beta)
        return beta;
    }
    return v;
  }
  // if b is a min node
  if (b.turn == 2) {
    int v = beta;
    for (int i = 0; i < ch_cnt; i++) {
      int v1 = minimax(ch[i], depth - 1, alpha, v, heuristic_choice);
      if (v1 < v)
        v = v1;
      if (v < alpha)
        return alpha;
    }

    return v;
  }
}

int main() {
  srand(time(0));
  int p1, p2;
  string players[] = {"Human agent",         "AI with heuristic-1",
                      "AI with heuristic-2", "AI with heuristic-3",
                      "AI with heuristic-4", "AI with heuristic-5",
                      "AI with heuristic-6"};

  int depth_for_heuristics[] = {7, 7, 7, 7, 7, 7, 10};

  cout << "Player options: \n";
  for (int i = 0; i < 7; i++) {
    cout << i + 1 << ". " << players[i] << "\n";
  }
  cout << "\nPlayer-1 : ";
  cin >> p1;
  p1--;
  cout << "\nPlayer-2 : ";
  cin >> p2;
  p2--;

  cout << "Total iterations: " << endl;
  int itr;
  cin >> itr;

  int p1_win = 0, p2_win = 0;

  for (int i = 0; i < itr; i++) {

    int random_choice = rand() % 2;
    int opt1, opt2;
    if (random_choice == 1) {
      opt1 = p1;
      opt2 = p2;
    } else {
      opt1 = p2;
      opt2 = p1;
    }

    Board state;
    while (true) {
      state.print();
      if (state.isLeaf()) {
        cout << "\n---GAME OVER---\n";
        int stones = 0;
        for (int i = 0; i < 14; i++) {
          if (i == 6 || i == 13) {
            state.bin[i] += stones;
            stones = 0;
          } else {
            stones += state.bin[i];
            state.bin[i] = 0;
          }
        }
        int diff = state.bin[6] - state.bin[13];
        if (diff > 0) {
          cout << "Player 1 wins!!!" << endl;
          if (random_choice == 1) {
            p1_win++;
          } else {
            p2_win++;
          }
        } else if (diff < 0) {
          cout << "Player 2 wins!!!" << endl;
          if (random_choice == 1) {
            p2_win++;
          } else {
            p1_win++;
          }
        } else {
          cout << "Match Draw!" << endl;
        }
        break;
      }

      int agent;
      if (state.turn == 1)
        agent = opt1;
      else
        agent = opt2;

      if (agent == 0) {
        state = human_agent(state);
      } else {
        vector<Board> children = generateChildren(state, agent);
        int sz = children.size();
        Board next_state;
        if (state.turn == 1) {
          // max node
          int val = -INF;
          for (int i = 0; i < sz; i++) {
            int tmp = minimax(children[i], depth_for_heuristics[agent - 1],
                              -INF, +INF, agent);
            if (tmp > val) {
              val = tmp;
              next_state = children[i];
            }
          }
        } else if (state.turn == 2) {
          // min node
          int val = INF;
          for (int i = 0; i < sz; i++) {
            int tmp = minimax(children[i], depth_for_heuristics[agent - 1],
                              -INF, +INF, agent);
            if (tmp < val) {
              val = tmp;
              next_state = children[i];
            }
          }
        }
        state = next_state;
      }
    }
  }
  cout << "Player 1 win = " << p1_win << endl;
  cout << "Player 2 win = " << p2_win << endl;

  return 0;
}