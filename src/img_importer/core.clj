(ns img-importer.core
  (:require [clojure.java.io :as io]
            [cheshire.core :refer :all]
            [clojure.pprint :refer :all]
            [clojure.string :refer :all]))

(def json "
  [{
   \"title\": \"Circle Cube\",
   \"name\": \"Paul Trillo\",
   \"invert\": true,
   \"background\": \"https://www.imagecu.be/serve/AMIfv95S43Lu-kXxeQ_SpzGct8hQcowd-0tnGTCRVwpKCaxV8jn6XiJtTESFmxin0N2MZbbkjSHUuWgm3aKdSPa_4j5oP4uw8rrfB5x4HoQHKvFkcYYDj2s4z-3ULugsusKmWo4yJSHbI03qMJelQZpVZ7TRkPCfFPE3cawJncOsjCqCTjFWmnM\",
   \"images\": [
              \"https://www.imagecu.be/serve/AMIfv950lXhmQP_6iWmxzLD26V1zGyzKlatNeTaCu0Ky7MNhntpl6WYMYs6MbG4DUW67245a4FBWS7U4uQnApgfKtaHKN7jEW7E9IaFiV6KodV3bRgnWvmGF_SyehtnW_BXzAevDHNjDHCK0uWKPs1G1FWDRg1tLs3Nw74bpGO2pTLf-KeSttOs\",
              \"https://www.imagecu.be/serve/AMIfv97JQh6N3IpDpfPD2agSHofXoh5IXCsyOqzO7MgqYAFddS4cwapiYEanNvoZfgVuTi3aUp9978A9_oLCIJxrfuLKlMSgXCTu-bW0XGcKSbqHmcwTHojXYF_fjujRUTND6JnfeQVi25MWH_WidY36TBvYIocYTvxdL-uy0r0K6WyzXFoNFlg\",
              \"https://www.imagecu.be/serve/AMIfv96bgRz1IClxYxrftXcbvORWyV-NGuyDzti3Vgw1lQCasXqhnfw_DaBXKbSyoow8bMW6uIDHwdG3oEtnX2AtyNpjd6BVMV-AAknEnowFji-ATle5RO0SdjKWC6I8wQ0hQ39-13kxTxFlWcsuU1i-tJX4K3u8bRaoks5Z3HWrgDmQsWplkWM\",
              \"https://www.imagecu.be/serve/AMIfv959CQhnWV67S8a2C2_MFpJoy2gyKldN6DuDPCBhGh-Iy7FWL0rNyCu6wjKzFdaa3Xk6XSX-x5UX0p86gDvofDnjgSRA5k9oXHwhqaVXs0mj62GdmFt3GgL_LFTiGrCW1VT1vMCJG4aFAetFIzGf3ZO-k4Ul7xsWzfUkvVs77bPr_voncLg\",
              \"https://www.imagecu.be/serve/AMIfv96gh-PaUbB7GHjLBWf3K9MKRjyM3_bbRCCotONd-lsC4MJ97XyJ8eFvi2h9CAIO78Wt-Nj0aiMXiw1TGq1BCTyUxPOr4eXxzPaWHwk2S2k8YyUUGV7z5GaCcckBpuPeefZ0LfXPewR5djcSnaDDVQ-347k-7_4-ICgTW2lCtIdhl2qY73s\",
              \"https://www.imagecu.be/serve/AMIfv94kQ9t40-ndnOqDALILZsbSqwpxvOc8UQ57_EMzrvdESzh9MuEzwqeCqlVvNgYta9dF8R6vNG7q8bvgXl6P9O6XqxF9HYdHBqpH56Ky6zy4EJf_T9Z9P4r-PmAr362YCHooCKdmbcMpo9pjVQUOpnLImy12AKxIVkznPPUjfervVZYLEE8\"
              ]
   },
  {
   \"name\": \"Wade Jeffree\",
   \"title\": \"Surprise Beauty\",
   \"invert\": true,
   \"background\": \"https://www.imagecu.be/serve/AMIfv94fBdGT7X5Ll6FlUXIu0Y0tE2-aGqQmeSj3b47coQwAy1MVjOkWZeSgXxe_d1WkV3qF6_s4Vjhe_HG_spz5L94oDVJ3XqGhZeQWTFEzfLRM3MZD-3fcId8BrTg6RI_a1AarHBJ1iLgtG43rCIxZZGy7si_zUcbbKl3u9zPGbn_AvEGJky0\",
   \"images\": [
              \"https://www.imagecu.be/serve/AMIfv97jTP90evjDW6uHSd7Q2-tz40g-dFT5K_U0rQlZqojDo5XH3nvYTIm78vI9Ut1fNHQ1lYks3qFZcsK8db4kdMyOjcJt_-pIsp8ZzpK-Fc9nv3naSpsGcrcFpJckUtvBvVtFIsS41tJkMPS0r1JM824mANOwW5yAZ8MIyao1UW9LBLuqc0Q\",
              \"https://www.imagecu.be/serve/AMIfv96MBm87oRyTxSIbIV7OCKHJFGgnpzixxd2O30duEPyPJiv-SxN6yu90dFdhd71nrM8BqgSmo32XGxfhH7pWeXgiph5owOa68PNtPKhiDPKxbOfhBaa62jZFhZwy2p4PPib15K4XV5cjNwA47E4EP8e9rLriNZ6azJZsc0cgopUNEgXH2og\",
              \"https://www.imagecu.be/serve/AMIfv9773MKfNkMuHtHqxcUDtDwc9CHUAhEGboTsyw2Jh1DvPgH_0RztRCKSSihnGJKgL8gYqJ6Csq6oPys2SWzJBCM4QFcrOrLCqbcA07cvv2jE2RNhkyc8A_CCuEynrzFBshmHziRYsQd8RNgv0Dht8S8y62mOQfomPgmDQNoz_TabiTx1mvE\",
              \"https://www.imagecu.be/serve/AMIfv94ngmLZie4Khp5CVOHeoH_w4LfSkUCEWcwUn3xgK8FQzqPhMCtBY1qS39C3zXkZJMqA_MSUb6Cudz_b7btaLzPjfWddWIFqvC6CwNJHUwQ2p0J2OPQfJNNg1yi3yvxvh6dMQAWRyk2rhxsyAsmxbjpWMGwqddPGEDvaFwh5Uzbwn3ATnho\",
              \"https://www.imagecu.be/serve/AMIfv94pD_8pYaGp6yVlKRmhzHuj5v9exmHw5yYsJAC9Lhzxm7pIQyC17WaSZpOUH4dVOF0J4IoKSH766rYYzLrvzG9wPTfMws0DQuwjS1VUEqeaj5k5JxUPhxk_iEDCwCIFslj4NiiFYtDgM5ivaCyYszD4bCBZn5EktC3FzXeRTJFkzK9Pe4A\",
              \"https://www.imagecu.be/serve/AMIfv97WnavQhRrvHtGrNCDo9AmY58mngv-v8GzrlBInfMd8321DuEM4xS9X-UxCe_O_13dH8XsUVFp1yhHp6VqyXobaVd-fYggCcT7-_gIApYrx5TyEy0MEd7slDZZSTB1MQoYfbE2gIM6pFaY70zLSiIDWo2bnOLYljlxmIaeK5q6IBhdEJXI\"
              ]
   },
  {
   \"name\": \"Jay Quercia\",
   \"title\": \"Game Face\",
   \"invert\": true,
   \"background\": \"https://www.imagecu.be/serve/AMIfv94L-Vu7ZZ9E2I9ZVNGVKdtR9cd-tgxOikTj68j6hetudd3KZ0kPrEfatjUgAmp7In2mpVMBEYAZ-6YBBsFRAqvGRVoKL2iab8GhIAySK1c64tMlofX1H012tisFVEkSVmkEqrnpqjLorX3N_LD7antAigOwRdgiCeS0SO1Tn10wDYEFZGc\",
   \"images\": [
              \"https://www.imagecu.be/serve/AMIfv97RE2ii7lPOjf-mMOSJFZkiLO3FuQy1eme1dQQUFLwF7IrX6ApccBzWsTGGF85UhxAnvJB3ubcQwqDgWITUlnsmRvbA9FOns5RNlV14rZq0f3_pKty613118_usJZ3fylRhV4s1Gg43ejRXK6Ky4tqWt3OzFnEV7f9OAWnEnRLQSnTEm4o\",
              \"https://www.imagecu.be/serve/AMIfv94OKT_MOdiw3rmNkV5FLGR78NH7O0aTeuFREeizW083g0d_xU7d1CoxbSMw64_nha0uMNRnA0ymVFG3Dw4hSySXcHzJXNGUEqLl81Yd6nKwAOEfjOFPGNhkaK8qlSo8iAHe5gqpjnx3u7uLeJlToI4BEJRLk92abg6LNf3oSbWn2DyGNv4\",
              \"https://www.imagecu.be/serve/AMIfv97bwi8RW4IxsWY575CqTqkEfdVHA5jJSdZ48GIIhvp4CfYIlHBHGA80KCc6nvhnEjT9YouLTjxntYGJBMdxPVTPqRdV8-WKM0ZlIV7wbH4u-6E_LnNL4YtoObyh5l9mwhtY_nBMzkQ510OBk8Hg-W4zNT9pmh7UI-qmsOExTNuRq_aXl3c\",
              \"https://www.imagecu.be/serve/AMIfv95CgEE3CuOTuXIj_L9IsZgr0qxXkPGjODjCl-U4K2Id5hNszq635z7q34fiJ7NuHs8TNea7wthzGE7ktzmqp81wKlN2tvJtuiqidqm9aHaF62l0-seZiHqhnCX9dzHR0mBBSRQPHlBKmDzv0sak-0-6bJp4_DbDfQKBX0Ej7SkSbUhRgEs\",
              \"https://www.imagecu.be/serve/AMIfv96PrtQVy3EeR-NvIV4M_pX2EBpjnPd39ESCso0inTfLWP2L5OESrskl6FwKIEn_AhvX2mnh8yNMq5nxjxmjnZ2hrbjXywmJkk_f9YktPqgo7zc0ZUKNMUSFDOLUPK4oOrIggPpYzJem03I6TIDep7soIPik2yB5tiSmJ4mi5kWytoFq9oE\",
              \"https://www.imagecu.be/serve/AMIfv94PDqDhKXDIWtR7LM93BgjO9x_dvMUutyiF_8v7mNawlNYcrsCIsiVzYS8w9RcD1B-OelyLNvNqapRhMqJM7rjIYkHZP6DqrrznjQwdaxv_aUF8-zw58yzEE_nmUhC0C5ygUxKnwcyX9TDMaO_dCwfPch7Y-dACOeiQiLwAiHGRVsY5e64\"
              ]
   },
  {
   \"name\": \"Matt Delbridge\",
   \"title\": \"City Cube\",
   \"background\": \"\",
   \"images\": [
              \"https://www.imagecu.be/serve/AMIfv965Nl_6hzMzEPd8h8Ls9k2VzAu23gKQb5od9oQdXbLK_XDBQgOGVMRhLWf_EXJxtD68z85Rd0xoStO8g5lIO7pvF77lNGKh42CbMeEyBWSg0fgjTSuEwG727mX2E6TUh5_YNRyASK5QxkUsCM2UA4kzkWRttP1FdEzbiEvatpyPSAwZbks\",
              \"https://www.imagecu.be/serve/AMIfv96uimXkz5lcV91Bnrk7mWU8mWuH-pBecHHHZHN_MbSe2dnnsPoC6eGCsWWIirQCA8TJEs6YwyTOIjCfMJJpKUIyez33kQtZKq811sNMxih_TAA9eFz8taN9Mj7TfA-VXXjsJsKf8EXRk89Sg3rIRGmxkRtChoioh4IPrJvVUOSdmdmevuY\",
              \"https://www.imagecu.be/serve/AMIfv96kfTRJRjs_6-Y38WQJ2uKCEnWtiP6q5NkVjUEYbzMCe4DssSNtaNByMvk6qDjC6kHDgtPJhGYcUAdBzoVMHbL6pTCG5jN3zNz5WSo2cos9X8SxzgRAycMpdJEFgFIi2P-QORwilvjIgdG6vqeUFX_evqgVsEA0E68AT5NdKpp6LiaG0VA\",
              \"https://www.imagecu.be/serve/AMIfv95CdY_MeNOIb9NyeVhZDmTShwFVzOv5RQGq4Ii9z5AcztVRcNaKjc2-HovWKxkVjK8NBUGF7US2WCFTWqwx7cA0sxH3a6TblIS-1bO2C6kCjFxwnWcXYcsnXO7sG1A-IYDEk83vXIFjNspmwNSmi0k8RFlmitw89JWVILtupbHpfxI-PGo\",
              \"https://www.imagecu.be/serve/AMIfv97PBaKol-BnO62lUq0Bw6sbgDQxl1WJ9UuTdLzm4J81rkizb2Ezgydn6d_Mkh3uG0fLzaSW21n7IYl-45NpvTGXIiLYIJ2LNT84LuqVEQkYz-XSqIT5viAVie2oLrqbUJOw7QIkU5DtU7rIL0H1Jesf7rW8WL8rciM4-U9risLdZtM3lTY\",
              \"https://www.imagecu.be/serve/AMIfv95hJTCilxBZohQJeJp4Blb5e0CF8f_WIb98wlolaX8ZgAlExmYSVyqG0FZVx5yWtpdjU6XQQ41O_Mo_nH-qUhjp7dzRU-5SMOU8OJt1KeRzZ_Nmuw-rwRzL0std4Xj3iSLl0VuOxtK-skX7FZXvY_hgcGakYrzV8e8lGS_6n4D-SOUDoLs\"
              ]
   },
  {
   \"name\": \"Jon Chonko\",
   \"title\": \"Sandwich Cube\",
   \"invert\": true,
   \"background\": \"https://www.imagecu.be/serve/AMIfv97EhypfvXDdzUnuGIAp6mQjxbytX8MG_tNZZu2i_5Q76LaFqkMv4Ka3miCTR8ih-4QkRlpkdjJ-VqhIc7HVFqZBTFXWBRNMrLiOrzFCMoFAhkHl8-ERnlrWpDS5ghz9tGTYFCTEJw8OQTmGU84tlLLD6x__BrGozxxuEutCIhC0NB5Dl7s\",
   \"images\": [
              \"https://www.imagecu.be/serve/AMIfv95eyG_BecQJ0BxFVEy6tLpJQlPx8zfWYNoQuyc5JfNhzgyqmAnGSqsDcS7d2VadY6wOS_A9299VewnR3vHX0ryMmLnc0fdwH1GSlkXWGEu3o0bQXUfTwNFEcpvC1UuMoOFTx0H1fvf4K6j7zPyKtsDLXi9xdyZib13pnhPefluXqu8ng3Q\",
              \"https://www.imagecu.be/serve/AMIfv94EiWm82HFuoz40Nel94JZfy8xSzaRfYmil92dKv6PWz3xtuCjlVt4-N_oRvp1AAk7CUh4O7nyLTKQdof3bFpVn5DkqqW43VWH_QmMzfkxaKniUqEXHDTc4K2iNJLBOWgJQMTz8sZ9e7r8qXTlYPJMXpCWT61gJ5SV4gW4Fqz0e7dbOg_E\",
              \"https://www.imagecu.be/serve/AMIfv967vVCs9Det4NBlqyM9UCrEzZllcAGi-6_-neNZPSP4ke8qPtyg0bOPG-zWtMoFCsR_EXVcyKC0Hidr19EZR0HkVTowFIMLfW9fNF9iA4HBcPksxV_gkm8HAS10Ga-HOOgLsgj-8z9NcLzT60b2AnLmEtJWeH2xHZt2JZq8b_9IponsiSU\",
              \"https://www.imagecu.be/serve/AMIfv974FUf78YVSlNwstMYsOilqqkxOfIjBH-4mRwuHIxZ358CNSwlcrYW0bZzPMeaRqDqTpN7zrztgDmFDbVft31_ogR2KHnYgEsk2v45DeH5M2sAbsXt_XyJ5knFFTTCqo7fRAoxKdSk12Pf--W88ER4k5yjZmTFsKpPVlLJpOfvBdq6avHU\",
              \"https://www.imagecu.be/serve/AMIfv95WF6CTFHNIK5YIkTOlG1hwlzgRNDqkso9_VfiDc-R7SxzqlBjqWUlgsy8YMJpz4RW3KrDX3fEkIKx8qEpfIao6ZpfZ2g2d4r_gEFHM9ENaumZr9YR0T0jNnDJY6D3XkWKT46FKkR-mC1MAMJ449CZVTBdu99vQYydQSoVsVmBd6nkDa_A\",
              \"https://www.imagecu.be/serve/AMIfv96V1luvNH3X-fmx1P7qhp4mdrcYh7GzL-w9ZGhSWyS-fREgyNhYBHHFMg_Kpg7KLJjAN_cetag_Byp7J1IJlYi_gkKT044FrtdRO2EdLUFHWpK9bLINjpCDE6va-6Z_rN_MNWE8f39OgHwZrubrjfKnMA2TdIqfO-NU6iqcMK34Fae32DA\"
              ]
   },
  {
   \"name\": \"Steve Rura\",
   \"title\": \"Static Cube\",
   \"invert\": true,
   \"background\": \"https://www.imagecu.be/serve/AMIfv97pvUYgOTbdsrvaLvc8UcWMcugk_me_4zrllXPbzA87YO-vUXvVJcnl3AIKdO2_UDZVJ69ZboZ0Toff8W1KlzaU_tKqbhbUoi0-YmfbLAs08U9kv-2iw6FfCCGm2BRNPivv3fnSNsEzoAxUYPZ4l69kgBTVVOijzUCvuMSbbAtU4Kd8WEc\",
   \"images\": [
              \"https://www.imagecu.be/serve/AMIfv95cM7nXpj-EEmYzbRruNYO2T7MmgMxJ_hsVALhswj9rBAnwMjXf3nDMV5d5mOtMBukYaPT3-7cn1J-69vNHpFj-vSNFEaFjDXpITLo1nyej432OJyhwlwfX4G7hciuAP2xsJqEU2oM14d-hbeyNnEq6AL4ZW6SSaOD57xgxO6Uzlst2R_o\",
              \"https://www.imagecu.be/serve/AMIfv94X7iQ_UrD3XmhqpeV9lTVL-YemsHeiCzIQBfgo0OYokfYZNgP4fZdvQr9nNdYYlIplb-4laWElb9s3gcwgwndO65F-HYXqNK4DMvWTq5nLtg1qSZgTPJ28FOMVe-_rqQYy7GaFgk0syQP_eejevyIJD1Z019gjnN_J4j5lPsyuGg8NVC8\",
              \"https://www.imagecu.be/serve/AMIfv96dw5vrKGRqoPixi7yEUDe0lIZEnCLUGSDosiIRfk15I7GVGyr0AyHapDPhuriIHhES6xG3fVXAnlUw4glLJ5NWqQQKCVVwEn0PzExDz-BWlIobueZSUrep_Q00qBRMZItXBd4pWtetVVNxzEQJmxB68Wsilb9jSIGkbquCevm6CvSlah8\",
              \"https://www.imagecu.be/serve/AMIfv951zRflgaVma55nOb4jAiAUknJKFoh1VcliKS_eRcCQMSkoaPfKpZRUXKAXfow2U2SweJOIqaxHnk7tnYKSKkn31yVQ-dOMJ0IYowAozswb3oh6wluW1GWfRp5LAsS6fHcimuRRxnItb8EvAonbbCiMv_bspemlDyVbuMV5fIj1OQehgd0\",
              \"https://www.imagecu.be/serve/AMIfv97mxjLIox-Y4xib6aY0rZ_l7-aw256VVhRdvqKWc8clK1_k9ZEpu95WUGmoKiIQa6ldnqh-4JBk3oqCbEhZRD20RD_LKtJkS6iltUAQIiW1IYl93VRklciY-bvUxhuX2y-v5jkc203tZl0qdUUP7TMw_hAPi0KCjhd4Z8i-VlrHYegfjss\",
              \"https://www.imagecu.be/serve/AMIfv9586csu7Crba2ID2jfwZhxEF7NXS4oOvmkM4LtGBMvFdtZFvlo17U9whjfNgRCKV0f3tGrvAupE-0QO86WHkOqS1LAFb_IH4ILPJEyaNKboCM07dK6ZgBnwLQ-WiYJYRKRhe02xu1EMO7pMWd44BG1JcIp4B7V2BIuD5omIxW05PTH38rI\"
              ]
   },
  {
   \"name\": \"Hubert and Fischer\",
   \"title\": \"Rubik Camouflage\",
   \"background\": \"https://www.imagecu.be/serve/AMIfv94KZQX29nn1oNU6uOTUyVl1RhgjYCR1kQBtFkKMgYZf5Gcln0EkFWYtF7545D3gcpRXQK8eoJR1lgJQZ5rltQ_HdTAROCH7Na6rCK9oA_P_i9UGc9cBOQVSEzkiyqD7dwJ045MaGbDQfYaZftjX_YFQCvqCxYaAgVB4AJ0qNKe7krARcuE\",
   \"images\": [
              \"https://www.imagecu.be/serve/AMIfv95jhajh3tviB4Yk1Vyj7GeZ5SHzLHtkQovBHWvznSa4_DFLraICm9HCSIkK0dHO3_7GE_FtWi58ftoCRT0jO5q5WANrofMRiyTBvGuRDcSCk3o-CB1podLx8abV8MDHqaetKpxx2NmAtaz8Qo2AqJjzNw5fY4gZhkygRjU1deZHyPROvc8\",
              \"https://www.imagecu.be/serve/AMIfv94iiJmp0RvJgUlFip6j1Unl1XSsACYxT_JKnkG0_rEfoKh8uWqkWQo8UTi2fDRfC6EMMXGaE_GoTwf4zkP1vvtEZoWysXkAItCJFRxJN2XIbFowHsifephXqHSj3fPgttAbNcAPK6rwclMyvikfJPTPc8sUUOYB3Kw_fE6mgnot1n_kxKc\",
              \"https://www.imagecu.be/serve/AMIfv97hxNlC4pIv9d4UbZNdFebs4sVCGvn3hO3HRrl4vFtPDEViSqu3XThptp3viL3upPfzYolYy8b1SvicMPsYO1gd278tzZ5zn038mud7qawS_3jvuqNM8rx_w8qrUOWAXVPdbEwzoFvxGAfOylB6D3Ginyym0SvrRiHgLDpqnno6UXIjfJg\",
              \"https://www.imagecu.be/serve/AMIfv96iLn_LBrD2QO1uXGpyvMmVms9GX7ewQ95NyWPzDMTtApvK6v_ujvEvVfxXeSjdkcg_Zrk6o4Ay8NpK-5LTgbLjd5MpyTxQOE6Zm78n-DY-2l7r8mdpf9HJRGYHgr8hruLZ7oJxy9UqP5Rjc8_aSMgouE0VaF-9gQP_00ubF4f73aRJHXE\",
              \"https://www.imagecu.be/serve/AMIfv97IvwyqFmja3wHH8rVHLVNIctjYpw4yjFlsLOOT0yOwBy573Twa9WBHJLjlAhzw1K-2clTWhe1wkfcqmN7hFwD-ocveQhegEVbMJd0mb6LW-AZlk--CnaK7q8cx5nXC1wz-2aTh3ZaQK3WoNKyJlXpvCCny5Ca1jq0Rxd-4o7rfNk0HBDs\",
              \"https://www.imagecu.be/serve/AMIfv96ouEtnTcWkE-LOba5krUTQYg4hjVpYTCLMGwp-dYSmCOuyIHbWv9TES30VU3kmNgj3vURWWoCPPU6FxdM2PDOut69bMAjRK-DhDwdnWYwGOLF5tgY8z2jF_vXw_x61RSZ6md6521c2QjRMEbHlVOJ2BdIJsmZLxet0CkeITwJl2Envi5Q\"
              ]
   },
  {
   \"name\": \"Glenn Cochon\",
   \"title\": \"RGB Cube\",
   \"background\": \"\",
   \"images\": [
              \"https://www.imagecu.be/serve/AMIfv97lsNrFBYiQ4i-UeupwWzVjIFC2TdX42_z4dLz-vFrXk5zQhkkXe4h5XXsEDGsF_3FhIza5Oc95f4uRzBv4AUMDKE9qIOcQoXKiujTjMfpza1zc42a9Hx3AT-6v4TmKAqwr1Z-aq9Z1jWBfw0qwrQ2NeeucTmNsKL4oK8-dtcvIt76AeVI\",
              \"https://www.imagecu.be/serve/AMIfv96gsDvvYV-7JBkFyg115v4E5HdoQRyJZvjw0j9WDz66fvOHLCzxy9JbFkchli4A_3BpMhcvloteEBKUns1IqfsoROP64fsRNpKStscEwrqZZDLQXvpkqhM1hcPlZ19spccoXXgbH-UjwhFQ8OXjZyVlL9yMaQox-03POcrOQGxALsfjc5E\",
              \"https://www.imagecu.be/serve/AMIfv9549uforPJEmAD-SROWR9L-V7BPeNJXy5xU09nlM1B6FD7z80AlmAq3-saJvRPc7qitm_JpDRZWbv8c0SSqKw0SejvYuqTL9GdT14ySrz9_vXnF9I3afn0q4ld1yRi0I-B-qyWpgNBhPRVShJ3Jk3xdqaOPkltOOGmVj6dGRFj8yKkz_6k\",
              \"https://www.imagecu.be/serve/AMIfv96Rd_PpiWvmqxfCCnIAsTC5bofhgIo0XQVTe8mh0m2IO3aal2r4e5HXAcJEB3mELudWcj2V84ir4yapf9BfkoEmA6C4HLfuIcVxCcqI62ciG2AgUOsGUX3p8ErJC_UXKWfG_3Zm6ivlm2lIrFi7oNNIlqvte0VrWnIITPvmE8x8o-hL2As\",
              \"https://www.imagecu.be/serve/AMIfv97iikxsYW6xYuLES7pY9v6PDjEmviYfLVQaF0d3rSJVSAuX-ta6C0In0mwV7fWO0Dsd-ZwQCppVU-IHuijO7mOOpKlaz7FVNwsmtpq1mq2H0AGoONLoSwCFV19CmPUVYDx86hJfi7cHDH0ERYP4uJ8Snz3SxlqcF73VuBe7kq9Eh4aYz7Y\",
              \"https://www.imagecu.be/serve/AMIfv95yChsqxGuNPqPvGz3b5nIrf04NzDnnmBoqxfs0WOcbQCfM3Faommbb0zRt8Oexip024mLK7vzIfbokInfWvoxBbTZ2tOPELKQ_REJEcwgjktzylCJzoqPYNSMm2w7OACYXIQ2HhlOBX_xH7aqr_N_E4qNePzBuZf_azcpb9MufaRWlpuQ\"
              ]
   },
  {
   \"title\": \"Sphere\",
   \"name\": \"Isaac Blankensmith\",
   \"invert\": true,
   \"background\": \"https://www.imagecu.be/serve/AMIfv96yel_kyU_czyy2us2pXPzovW2IfO0XvVteLiLgJBfmKwTEPS0mvOrJA_lcywYbWadofF-IRGUqPRYrMZQgsKt5zmJHbL25_wx_9RpjBhvujgGreZp4BTth4QSykC2nAKtXGEExdxQr9_wZetLiKeXhEk1rnzzcpcwIZZSh1hoItWVIEWs\",
   \"images\": [
              \"https://www.imagecu.be/serve/AMIfv97xDUlPyhDrZOC3qU2nKPTHJTugIZur-esMzAhH9d8uLelMGgKkIR7Pt4yzesyaS14WMKPnSs9dCvb3tlp6itDBUGNPjVqlxYwIfFoRMtaZizeqH3CWVNeeFEF2918FYPb7mvpQmFegNEcUDecfl0fakHrV0bGmQSDHUrF0K7_2bdTYxUc\",
              \"https://www.imagecu.be/serve/AMIfv948eeCH6VNLFC2OgqF9lan4mKyCiD3TRXQEGxclIqloaQYbROxZ7pgLasY-9O6R2kbOtwL2t3KKRSPZBL5Rdq1wNqqIqbaJ4gqtxzWBRF8Ew7GpS-NV4reL_4s2UgA2nv2Vwm_i5TfHU4HBAPVZYUaBDD3X28lufzs_oLugOIOrocKG7s0\",
              \"https://www.imagecu.be/serve/AMIfv95bkCIT6RljWzROrpGZTYmu1LhfT14U3eW3ZMQwHuq9r9IU9t_FFeEqZsiw-Q9Ur7NYdN4XbOWrnTFBRzcK9rFt6p5VX5RaqsMRReohc1yyweKjniHimJEZWjzEszjDmigwC-T4rL2C4qG8DTetBNzovZsQh-tls68-caXL2ej-g5hkeKY\",
              \"https://www.imagecu.be/serve/AMIfv97f1npGmQz_QzJzhvYqZ-vuWCZI6EclhiOiuvGMfJ7o-YVtbydJ23M4KnMVnGFzDk_AX-mG4p5-UlEREZ7zDBA6LuPb7M0pyBNlcbSLs1PtZc7MOWEIr1DFuYGFSmxXzhOImL06Z54aemeSwcqGEQytTNWm5ghm4RvkYN9fIik1lHyGIz4\",
              \"https://www.imagecu.be/serve/AMIfv96t0-vVnIWN5jvqg3bmb_4XMS8VRD0O8z9BaN-DS2ncgotGKKJd0q4qHBCsij6NHgEV50q4eaflZkMkwrQle9mzt4dli44R6qC998UnKI7FL_vOQcuKhIWu_J9c2O73m8O5Dm3319zgoVYj3hoPQ-X8mbVjPk0ZyMkoD1A71sKQ1pNMu1A\",
              \"https://www.imagecu.be/serve/AMIfv96_uGBV1gfn5dDNnP62UDgtv8-89xBMRiRUV6v35jWkti7pulZZN3tlFQeb6BRlS0HGFcAi71Oc4x0fCTcp5fKG0v4NM0-9h9ZxTwJEtTimL2QXyUJAD7ofU_I-Di5DwUXLFj1nndICm3YqaPGtofpIScpxpk6XNm8PIo-TkEtsR229wvA\"
              ]
   }]")

(def edn (parse-string json))

(defn copy [uri file]
  (pprint uri)
  (with-open [in (io/input-stream uri)
              out (io/output-stream file)]
    (pprint (str "saving: " uri " as " file))
    (io/copy in out)))

(defn indexed [f v]
  (let [idv (map vector (iterate inc 0) v)]
    (doseq [[index value] idv]
      (f value index))))



(doseq [{:strs [images title]} edn]
  (let [path (replace (lower-case title) #" " "_")]
    (indexed (fn [img i]
               (copy img (str "resources/cubes/" path "/" i))) images)))

(copy "https://www.imagecu.be/serve/AMIfv950lXhmQP_6iWmxzLD26V1zGyzKlatNeTaCu0Ky7MNhntpl6WYMYs6MbG4DUW67245a4FBWS7U4uQnApgfKtaHKN7jEW7E9IaFiV6KodV3bRgnWvmGF_SyehtnW_BXzAevDHNjDHCK0uWKPs1G1FWDRg1tLs3Nw74bpGO2pTLf-KeSttOs" "resources/test.jpg")
