#!/bin/bash
cd /home/kavia/workspace/code-generation/football-live-score-and-news-platform-38-49/frontend_react_app
./gradlew lint
LINT_EXIT_CODE=$?
if [ $LINT_EXIT_CODE -ne 0 ]; then
   exit 1
fi

