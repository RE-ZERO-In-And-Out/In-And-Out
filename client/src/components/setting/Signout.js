import styled from "styled-components";

import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import { recoverInitiateSchema } from "../../schema/form_validation";
import { loginStore, useStore2 } from "../../store/store.js";
import { useNavigate } from "react-router-dom";

import { Button, TextField, FormControl, Grid, Box } from "@mui/material/";

export default function Signout() {
  const navigate = useNavigate();
  const {
    id,
    setId,
    setPassword,
    setNickname,
    setPhoneNumber,
    setBirthdate,
    setResidence,
    setGender,
  } = loginStore();

  const { setLogState } = useStore2();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(recoverInitiateSchema),
  });

  async function signout() {
    await fetch(`http://localhost:4000/users/${id}`, {
      method: "DELETE",
    });

    alert("회원 탈퇴 완료");
  }

  const onSubmit = async (e) => {
    const response = await fetch(`http://localhost:4000/users`);
    if (response.ok) {
      const users = await response.json();
      const user = users.find((user) => user.id === id);
      if (user.password !== e["pw"]) {
        alert("비밀번호가 맞지 않습니다.");
        throw new Error("비밀번호가 맞지 않습니다.");
      }
    } else {
      throw new Error("서버 통신이 원할하지 않습니다.");
    }
    await signout();
    setLogState(false);
    setId("");
    setNickname("");
    setPhoneNumber("");
    setBirthdate("");
    setResidence("");
    setGender("");
    setPassword("");
    sessionStorage.clear();
    navigate("/");
  };

  return (
    <>
      <Box
        component="form"
        noValidate
        onSubmit={handleSubmit(onSubmit)}
        sx={{ mt: 20, ml: 15, display: "flex", justifyContent: "center" }}
      >
        <FormControl component="fieldset" variant="standard">
          <Grid container spacing={2}>
            <Grid item xs={7}>
              <TextField
                required
                fullWidth
                type="password"
                id="password"
                name="password"
                label="비밀번호"
                error={!!errors.pw}
                {...register("pw")}
                helperText={errors.pw?.message}
              />
            </Grid>
            <Grid item xs={7}>
              <TextField
                required
                fullWidth
                type="password"
                id="rePassword"
                name="rePassword"
                label="비밀번호 확인"
                error={!!errors.passwordConfirm}
                {...register("passwordConfirm")}
                helperText={errors.passwordConfirm?.message}
              />
            </Grid>
          </Grid>
          <Button
            type="submit"
            variant="contained"
            sx={{ mt: 3, mb: 2, width: "58%" }}
            size="large"
          >
            탈퇴
          </Button>
        </FormControl>
      </Box>
    </>
  );
}

const Alert = styled.span`
  font-size: 15px;
`;

const Page = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
`;

const TextInput = styled.div`
  margin-top: 30px;
  width: 300px;
  height: 50px;

  display: flex;
  flex-direction: column;
`;

const ButtonInput = styled.div`
  margin-top: 30px;
  width: 300px;
  height: 50px;
  display: flex;
  align-items: center;
`;
