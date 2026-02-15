# Agent Development Notes

This file documents AI agent interactions and decisions made during development.

---

## Session: 2026-02-01 - Documentation PowerShell Migration

**Date**: February 1, 2026  
**Agent**: GitHub Copilot CLI  
**Context**: Post-implementation documentation cleanup

### Issue Identified
After building the complete Order v Chaos application, the documentation (README.md, DOCKER.md, setup.bat) did not accurately reflect the actual development workflow. The app was built and run using PowerShell commands on Windows, but documentation showed bash/batch commands.

### Changes Made

#### 1. README.md Updates
- **Changed**: Test command from `./gradlew test` to `.\gradlew.bat test`
- **Added**: "Common Issues" troubleshooting section with PowerShell solutions
  - JAVA_HOME configuration
  - DATABASE_PASSWORD environment variable
  - Database migration issues
  - Port conflicts
- **Updated**: All environment variable examples to use `$env:VAR = "value"` syntax
- **Impact**: Documentation now matches actual Windows/PowerShell workflow

#### 2. DOCKER.md PowerShell Conversion
- **Replaced**: All bash commands with PowerShell equivalents
  - `cp` → `Copy-Item`
  - `echo > file` → `Out-File -FilePath`
  - `cat file | cmd` → `Get-Content file | cmd`
  - Bash line continuation `\` → PowerShell backtick `` ` ``
- **Updated**: All code examples to use PowerShell syntax
- **Impact**: Docker workflows now copy-paste ready for Windows users

#### 3. Setup Script Modernization
- **Removed**: `setup.bat` (old DOS batch file)
- **Created**: `setup.ps1` (modern PowerShell script)
  - Checks for Java at JetBrains Toolbox locations
  - Verifies PostgreSQL installation
  - Provides copy-paste commands for database setup
  - Better error messages and guidance
- **Impact**: Improved first-run experience on Windows

#### 4. Quick Reference Guide
- **Created**: `QUICKSTART.md` with PowerShell focus
  - First-time setup steps
  - Daily development commands
  - Docker quick commands
  - Common troubleshooting
  - PowerShell tips and tricks
  - API testing examples with `Invoke-WebRequest`
- **Impact**: Developers can get started in minutes with copy-paste commands

#### 5. Error Handling Improvements
- **Enhanced**: `Database.kt` with Flyway completion logging
  - Added log message when migrations finish
  - Helps diagnose Docker timing issues
- **Enhanced**: `BattleScheduler.kt` with try-catch in `ensureCurrentBattle()`
  - Prevents silent failures during initialization
  - Logs errors when battle creation fails
- **Impact**: Better debugging for Docker race conditions

### Key Decisions

1. **PowerShell over Batch**: Modern Windows development uses PowerShell, not CMD/batch
2. **Consistency**: ALL examples now use same syntax (no mixing bash/PowerShell)
3. **Copy-Paste Ready**: Every command can be directly copied without modification
4. **Windows-First**: Documentation assumes Windows environment (matches actual dev setup)
5. **Troubleshooting Focus**: Added common issues section based on actual problems encountered

### Technical Notes

**PowerShell Environment Variables**:
```powershell
# Set for current session
$env:DATABASE_PASSWORD = "value"

# Multiple variables
$env:VAR1 = "value1"; $env:VAR2 = "value2"

# Check value
echo $env:VAR

# Remove
Remove-Item Env:\VAR
```

**Gradle on Windows**:
- Always use `.\gradlew.bat` (not `./gradlew` or `gradle`)
- Backslashes in paths: `src\main\kotlin`
- PowerShell handles spaces in paths better than CMD

**Docker Compose on Windows**:
- Line continuation: backtick (`` ` ``) at end of line
- Paths in volumes: Can use forward slashes in docker-compose.yml
- Environment files: Must be ASCII/UTF-8 without BOM

### Files Modified
- `README.md` - Comprehensive PowerShell update
- `DOCKER.md` - Complete bash→PowerShell conversion
- `setup.bat` → `setup.ps1` - Script modernization
- `Database.kt` - Added migration completion logging
- `BattleScheduler.kt` - Added error handling in ensureCurrentBattle()

### Files Created
- `QUICKSTART.md` - PowerShell quick reference guide
- `AGENTS.md` - This file

### Git Commits
1. `3f7ed57` - "Update documentation to use PowerShell commands and improve error handling"
2. `6bff561` - "Add PowerShell quick reference guide"

### Lessons Learned

1. **Document As You Build**: Keep docs in sync with actual commands used
2. **Platform Consistency**: Pick one platform style (Windows/Linux) and stick to it
3. **Real Commands**: Use the actual commands that work, not idealized cross-platform ones
4. **Troubleshooting Is Key**: Document the problems you actually encountered
5. **Quick Start Matters**: Developers want copy-paste commands that work immediately

### Future Considerations

- Consider adding Linux/macOS variants if cross-platform users need them
- Could add shell detection script to README that shows appropriate commands
- Might want to test Docker setup on fresh Windows machine to validate docs
- Consider adding video/GIF of setup process

---

## Session Template for Future Updates

```markdown
## Session: YYYY-MM-DD - [Brief Title]

**Date**: [Date]
**Agent**: [Agent Name]
**Context**: [What prompted this work]

### Issue Identified
[What problem was being solved]

### Changes Made
[Bullet list of changes]

### Key Decisions
[Important choices and rationale]

### Technical Notes
[Code snippets, commands, gotchas]

### Files Modified
[List of changed files]

### Git Commits
[Commit hashes and messages]

### Lessons Learned
[Takeaways for future work]
```

---

## Agent Guidelines for This Project

### PowerShell Syntax Standards
- Use `$env:VAR = "value"` for environment variables
- Use `.\gradlew.bat` for Gradle commands
- Use `Copy-Item`, `Get-Content`, `Out-File` for file operations
- Use backtick (`` ` ``) for line continuation
- Use `Invoke-WebRequest` for HTTP testing

### Code Style
- Kotlin: Follow existing conventions in codebase
- SQL: Uppercase keywords, snake_case for identifiers
- JavaScript: Vanilla JS, no frameworks
- Comments: Only where clarification needed

### Documentation Standards
- All commands must be copy-paste ready
- Include troubleshooting for common issues
- Provide both quick start and detailed explanations
- Prefer PowerShell over bash for Windows-focused project
- Include actual output examples where helpful

### Testing Approach
- Run after every significant change
- Test both local and Docker deployments
- Verify API endpoints with actual requests
- Check browser functionality end-to-end
- Document any failures or issues encountered

---

*This file should be updated after each significant agent session or when architectural decisions are made.*
