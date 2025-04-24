#include <stdio.h>
#include <stdlib.h>
#include <readline/readline.h>
#include <readline/history.h>
#include <string.h>

typedef struct {
    float health;
    int score;
    struct {
        double x;
        double y;
    } position;
} Player;

Player player = {100.0f, 0, {0.0, 0.0}};

// Command definitions
typedef struct {
    const char *name;
    const char *help;
    void (*func)(const char*);
} Command;

// Command implementations
void cmd_update(const char *arg) {
    player.health -= 5.0f;
    player.score += 10;
    player.position.x += 1.0;
    player.position.y += 0.5;
    printf("Game state updated!\n");
}

void cmd_print(const char *arg) {
    printf("\nCurrent State:\n");
    printf("Health: %.2f\n", player.health);
    printf("Score: %d\n", player.score);
    printf("Position: (%.2f, %.2f)\n", player.position.x, player.position.y);
}

void cmd_scan(const char *arg) {
    printf("\nMemory Addresses:\n");
    printf("player:     %p\n", &player);
    printf("- health:   %p\n", &player.health);
    printf("- score:    %p\n", &player.score);
    printf("- position: %p\n", &player.position);
    printf("cmd_update: %p\n", cmd_update);
}

void cmd_help(const char *arg);

// Command list
Command commands[] = {
    {"update", "Update game state", cmd_update},
    {"print", "Print current state", cmd_print},
    {"scan", "Show memory addresses", cmd_scan},
    {"help", "Show this help", cmd_help},
    {NULL, NULL, NULL}
};

// Autocomplete generator
char* generator(const char *text, int state) {
    static int list_index, len;
    if (!state) {
        list_index = 0;
        len = strlen(text);
    }
    
    Command *cmd;
    while ((cmd = &commands[list_index++])->name) {
        if (strncmp(cmd->name, text, len) == 0) {
            return strdup(cmd->name);
        }
    }
    return NULL;
}

// Autocomplete setup
char** completer(const char *text, int start, int end) {
    rl_attempted_completion_over = 1;
    return rl_completion_matches(text, generator);
}

void cmd_help(const char *arg) {
    printf("\nAvailable commands:\n");
    Command *cmd = commands;
    while (cmd->name) {
        printf("  %-8s %s\n", cmd->name, cmd->help);
        cmd++;
    }
}

int main() {
    printf("Game Debug Console\n");
//    printf("PID: %d\n", getpid());
    printf("Type 'help' for commands\n\n");

    // Setup readline
    rl_attempted_completion_function = completer;
    rl_bind_key('\t', rl_complete);

    while (1) {
        char *input = readline("game> ");
        if (!input) break;
        
        if (*input) add_history(input);

        // Find matching command
        Command *cmd = commands;
        while (cmd->name && strcmp(input, cmd->name)) cmd++;
        
        if (cmd->name) {
            cmd->func("");
        } else if (strcmp(input, "exit") == 0) {
            free(input);
            break;
        } else {
            printf("Unknown command. Try 'help'\n");
        }
        
        free(input);
    }

    return 0;
}
