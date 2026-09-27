# Gastos Automáticos — compilação gratuita no GitHub

Este projeto já vem com um workflow do GitHub Actions para gerar um APK Android sem instalar Android Studio.

## Como gerar o APK

1. Crie um repositório no GitHub.
2. Envie o conteúdo desta pasta para a raiz do repositório. Não envie a pasta `GastosAutomaticos` como uma pasta dentro de outra pasta.
3. No GitHub, abra **Actions**.
4. Selecione **Gerar APK**.
5. Clique em **Run workflow**.
6. Quando terminar, abra a execução concluída.
7. Em **Artifacts**, baixe `GastosAutomaticos-APK`.
8. Dentro do ZIP baixado estará `app-debug.apk`.

O APK é uma build Debug e pode ser instalado diretamente em um aparelho Android. O aplicativo não contém credenciais bancárias.

## O que esta versão faz

- Cadastro manual de gastos.
- Leitura de notificações, após o usuário conceder o acesso nas configurações do Android.
- Identificação básica de valores em reais.
- Categorização automática por palavras-chave.
- Histórico e total mensal.
- Dados armazenados localmente.

## Próxima etapa

A integração Open Finance com Next, PicPay e Bradesco deve ser feita por um fluxo oficial de consentimento e por um provedor compatível. Senhas bancárias não devem ser colocadas no aplicativo.
