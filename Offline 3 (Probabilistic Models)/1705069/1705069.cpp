#include <bits/stdc++.h>
#include <iostream>
#include <ostream>
#include <vector>
using namespace std;

void print_matrix(double **mat, int n, int m) {
  cout.precision(4);
  for (int i = 0; i < n; i++) {
    for (int j = 0; j < m; j++) {

      cout << fixed << mat[i][j] << " ";
    }
    cout << endl;
  }
  cout << endl;
}

pair<int, vector<pair<int, int>>> total_adj_moves(double **mat, int n, int m,
                                                  int x, int y) {
  double moves[4] = {0.0, 0.0, 0.0, 0.0};
  vector<pair<int, int>> movedir;

  if (y - 1 >= 0) {
    moves[0] = mat[x][y - 1];
    movedir.push_back({x, y - 1});
  }

  if (x - 1 >= 0) {
    moves[1] = mat[x - 1][y];
    movedir.push_back({x - 1, y});
  }

  if (x + 1 < n) {
    moves[2] = mat[x + 1][y];
    movedir.push_back({x + 1, y});
  }

  if (y + 1 < m) {
    moves[3] = mat[x][y + 1];
    movedir.push_back({x, y + 1});
  }

  int count = 0;
  for (int i = 0; i < 4; i++) {
    if (moves[i] > 0.0)
      count++;
  }
  return {count, movedir};
}

pair<int, vector<pair<int, int>>> total_diag_moves(double **mat, int n, int m,
                                                   int x, int y) {
  double moves[4] = {0.0, 0.0, 0.0, 0.0};
  vector<pair<int, int>> movedir;

  if (x + 1 < n && y - 1 >= 0) {
    moves[0] = mat[x + 1][y - 1];
    movedir.push_back({x + 1, y - 1});
  }

  if (x - 1 >= 0 && y - 1 >= 0) {
    moves[1] = mat[x - 1][y - 1];
    movedir.push_back({x - 1, y - 1});
  }

  if (x - 1 >= 0 && y + 1 < m) {
    moves[2] = mat[x - 1][y + 1];
    movedir.push_back({x - 1, y + 1});
  }

  if (x + 1 < n && y + 1 < m) {
    moves[3] = mat[x + 1][y + 1];
    movedir.push_back({x + 1, y + 1});
  }

  movedir.push_back({x, y});

  int count = 0;
  for (int i = 0; i < 4; i++) {
    if (moves[i] > 0.0)
      count++;
  }
  return {count + 1, movedir};
}

int main() {
  int n, m, k;
  double p_adj, p_diag, p_correct, p_wrong;
  p_adj = 0.9;
  p_correct = 0.8;
  p_diag = 1 - p_adj;
  p_wrong = 1 - p_correct;

  cin >> n >> m >> k;

  double **init = new double *[n];
  for (int i = 0; i < n; ++i)
    init[i] = new double[m];

  for (int i = 0; i < k; i++) {
    int r, c;
    cin >> r >> c;
    init[r][c] = -1.0;
  }

  // step 1 - initial probability
  for (int i = 0; i < n; i++) {
    for (int j = 0; j < m; j++) {
      if (init[i][j] == 0.0)
        init[i][j] = 1.0 / (1.0 * n * m - k);
      else
        init[i][j] = 0.0;
    }
  }

  print_matrix(init, n, m);

  double **prob = new double *[n];
  for (int i = 0; i < n; ++i)
    prob[i] = new double[m];

  for (int i = 0; i < n; i++) {
    for (int j = 0; j < m; j++) {
      prob[i][j] = init[i][j];
    }
  }

  while (true) {

    char choice;
    cin >> choice;

    if (choice == 'R') {
      int u, v, b;
      cin >> u >> v >> b;

      // update probability for one time step

      for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
          if (init[i][j] == 0.0) {
            prob[i][j] = 0.0;
            continue;
          }

          auto adj_moves = total_adj_moves(init, n, m, i, j);
          int n_adj_moves = adj_moves.first;
          for (auto itr = adj_moves.second.begin();
               itr != adj_moves.second.end(); itr++) {
            int moves =
                total_adj_moves(init, n, m, itr->first, itr->second).first;
            prob[i][j] =
                (1.0 / moves) * (p_adj) * (init[itr->first][itr->second]);
          }
        }
      }
      for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
          if (init[i][j] == 0.0) {
            prob[i][j] = 0.0;
            continue;
          }
          auto diag_moves = total_diag_moves(init, n, m, i, j);
          int n_diag_moves = diag_moves.first;
          for (auto itr = diag_moves.second.begin();
               itr != diag_moves.second.end(); itr++) {
            int moves =
                total_diag_moves(init, n, m, itr->first, itr->second).first;
            prob[i][j] +=
                (1.0 / moves) * (p_diag) * (init[itr->first][itr->second]);
          }
        }
      }

      // step 3 - get unnormalized probability by considering sensor correctness

      auto adj_moves = total_adj_moves(init, n, m, u, v);
      auto diag_moves = total_diag_moves(init, n, m, u, v);

      bool adj_matrix[n][m];

      for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
          adj_matrix[i][j] = 0;
        }
      }

      for (auto itr = adj_moves.second.begin(); itr != adj_moves.second.end();
           itr++) {
        adj_matrix[itr->first][itr->second] = 1;
      }

      for (auto itr = diag_moves.second.begin(); itr != diag_moves.second.end();
           itr++) {
        adj_matrix[itr->first][itr->second] = 1;
      }

      double sum = 0.0;
      for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {

          if (adj_matrix[i][j]) {
            if (b)
              prob[i][j] = prob[i][j] * p_correct;
            else
              prob[i][j] = prob[i][j] * p_wrong;
          } else {
            if (b)
              prob[i][j] = prob[i][j] * p_wrong;
            else
              prob[i][j] = prob[i][j] * p_correct;
          }
        }
      }

      // Step 4 - Normalize

      sum = 0.0;
      for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
          sum += prob[i][j];
        }
      }

      for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
          init[i][j] = prob[i][j] / sum;
        }
      }

      sum = 0.0;
      for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
          sum += init[i][j];
        }
      }

      print_matrix(init, n, m);
      cout << "Sum: " << sum << endl;

    } else if (choice == 'C') {
      // print most probable position

      double max_p = 0;
      int max_x, max_y;

      for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
          if (init[i][j] > max_p) {
            max_p = init[i][j];
            max_x = i;
            max_y = j;
          }
        }
      }

      cout << "Casper is most likely to be at (" << max_x << ", " << max_y
           << ")" << endl;
    } else if (choice == 'Q') {
      break;
    }

    cout << endl;
  }

  cout << "Goodbye Casper!\n";
}