# 1ms-helper

1ms-helper is a terminal-based application built with Go and the `tview` library. It provides a menu-driven interface for managing services and viewing logs.

## Features

- **Service Management**: Start and manage services.
- **Log Viewer**: View real-time logs.

## Requirements

- [颜色组件库：github.com/gookit/color](github.com/gookit/color)
- [控制台库：github.com/spf13/cobra](github.com/spf13/cobra)

## Installation

1. Clone the repository:
    ```sh
    git clone https://cnb.cool/mliev/1ms.run/1ms-helper.git
    ```
2. Navigate to the project directory:
    ```sh
    cd 1ms-helper
    ```
3. Install dependencies:
    ```sh
    go mod tidy
    ```

## Usage

Run the application:
```sh
go run main.go
```

## 打包 提示:删除上次打包的dist目录
```shell
goreleaser release --snapshot --clean
```